import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import org.jocl.CL;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

public class TextSearch {

    public Result searchSerial(String filePath, String word) {
        long startTime = System.currentTimeMillis();
        int occurrences = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                occurrences += countWordInLine(line, word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        return new Result("SerialCPU", occurrences, endTime - startTime);
    }

    public Result searchParallelCpu(String filePath, String word) {
        long startTime = System.currentTimeMillis();
        ForkJoinPool pool = new ForkJoinPool();
        int occurrences = 0;
    
        try {
            List<String> lines = Files.readAllLines(new File(filePath).toPath());
            occurrences = pool.invoke(new ParallelTask(lines, word, 0, lines.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        long endTime = System.currentTimeMillis();
        return new Result("ParallelCPU", occurrences, endTime - startTime);
    }    

    public Result searchParallelGpu(String filePath, String word) {
        long startTime = System.currentTimeMillis();
        int occurrences = 0;
    
        try {
            String text = new String(Files.readAllBytes(new File(filePath).toPath())).toLowerCase();
            word = word.toLowerCase();
            byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
            byte[] wordBytes = word.getBytes(StandardCharsets.UTF_8);
    
            //Configuração do OpenCL
            CL.setExceptionsEnabled(true);
            int[] numPlatformsArray = new int[1];
            clGetPlatformIDs(0, null, numPlatformsArray);
    
            cl_platform_id[] platforms = new cl_platform_id[numPlatformsArray[0]];
            clGetPlatformIDs(platforms.length, platforms, null);
    
            cl_platform_id platform = platforms[0];
            cl_device_id[] devices = new cl_device_id[1];
            clGetDeviceIDs(platform, CL.CL_DEVICE_TYPE_ALL, 1, devices, null);
    
            cl_device_id device = devices[0];
            cl_context context = clCreateContext(null, 1, new cl_device_id[]{device}, null, null, null);
            cl_command_queue commandQueue = clCreateCommandQueue(context, device, 0, null);
    
            //buffers de memória
            cl_mem textMem = clCreateBuffer(context, CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR,
                    Sizeof.cl_char * textBytes.length, Pointer.to(textBytes), null);
            cl_mem wordMem = clCreateBuffer(context, CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR,
                    Sizeof.cl_char * wordBytes.length, Pointer.to(wordBytes), null);
            cl_mem resultMem = clCreateBuffer(context, CL.CL_MEM_WRITE_ONLY,
                    Sizeof.cl_int * (textBytes.length - wordBytes.length + 1), null, null);
    
            //Código kernel OpenCL
            String kernelSource =
                    "__kernel void count_word(__global const char* text, __global const char* word, __global int* results, int word_length, int text_length) {"
                    + "    int id = get_global_id(0);"
                    + "    if (id + word_length <= text_length) {"
                    + "        int match = 1;"
                    + "        for (int i = 0; i < word_length; i++) {"
                    + "            if (text[id + i] != word[i]) {"
                    + "                match = 0;"
                    + "                break;"
                    + "            }"
                    + "        }"
                    + "        results[id] = match;"
                    + "    } else {"
                    + "        results[id] = 0;"
                    + "    }"
                    + "}";
    
            cl_program program = clCreateProgramWithSource(context, 1, new String[]{kernelSource}, null, null);
            clBuildProgram(program, 0, null, null, null, null);
    
            byte[] log = new byte[1024];
            CL.clGetProgramBuildInfo(program, device, CL.CL_PROGRAM_BUILD_LOG, log.length, Pointer.to(log), null);
            System.out.println("Build log: " + new String(log, StandardCharsets.UTF_8));
    
            cl_kernel kernel = clCreateKernel(program, "count_word", null);
    
            // argumentos do kernel
            clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(textMem));
            clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(wordMem));
            clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(resultMem));
            clSetKernelArg(kernel, 3, Sizeof.cl_int, Pointer.to(new int[]{wordBytes.length}));
            clSetKernelArg(kernel, 4, Sizeof.cl_int, Pointer.to(new int[]{textBytes.length}));
    
            // Executar kernel
            long globalWorkSize[] = new long[]{textBytes.length - wordBytes.length + 1};
            clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, globalWorkSize, null, 0, null, null);
    
            // Ler os resultados da GPU
            int[] results = new int[textBytes.length - wordBytes.length + 1];
            clEnqueueReadBuffer(commandQueue, resultMem, CL.CL_TRUE, 0, Sizeof.cl_int * results.length, Pointer.to(results), 0, null, null);
    
            for (int result : results) {
                occurrences += result;
            }
    
            clReleaseMemObject(textMem);
            clReleaseMemObject(wordMem);
            clReleaseMemObject(resultMem);
            clReleaseKernel(kernel);
            clReleaseProgram(program);
            clReleaseCommandQueue(commandQueue);
            clReleaseContext(context);
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        long endTime = System.currentTimeMillis();
        return new Result("ParallelGPU", occurrences, endTime - startTime);
    }
    
    




    
    private int countWordInLine(String line, String word) {
        return (int) Arrays.stream(line.split("\\W+"))
                .filter(w -> w.equalsIgnoreCase(word))
                .count();
    }
}
