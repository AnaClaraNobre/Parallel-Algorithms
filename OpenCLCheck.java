import org.jocl.*;

public class OpenCLCheck {
    public static void main(String[] args) {
        CL.setExceptionsEnabled(true);

        int[] numPlatforms = new int[1];
        int result = CL.clGetPlatformIDs(0, null, numPlatforms);

        if (result == CL.CL_SUCCESS && numPlatforms[0] > 0) {
            System.out.println("Plataformas OpenCL disponíveis: " + numPlatforms[0]);
        } else {
            System.err.println("Nenhuma plataforma OpenCL foi encontrada.");
        }
    }
}
