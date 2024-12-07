Análise comparativa de algoritmos com uso de paralelismo.
Ana Clara de Sá Pinheiro Nobre - 2210435
Angelo Barcelos Rodrigues -1810748

Resumo:
Neste trabalho vamos mostrar uma análise do desempenho de diferentes algoritmos de busca em textos, implementados em Java, buscando explorar as abordagens de processamento serial e paralelo aprendidos em sala de aula. 
Nosso objetivo principal foi avaliar e comparar o tempo de execução de três métodos: o método serial executado em CPU (SerialCPU), o método paralelo utilizando multithreading em CPU (ParallelCPU) e o método paralelo em GPU utilizando OpenCL (ParallelGPU). 
Para isso, foram desenvolvidos algoritmos específicos, os quais processam textos e buscam palavras específicas, registrando as ocorrências e o tempo de execução. 
Os resultados foram armazenados em arquivos CSV e apresentados em forma de gráficos para uma análise visual e comparativa. 
Este trabalho contribui para o entendimento do impacto do processamento paralelo e serial sobre o desempenho de diferentes abordagens de execução em sistemas computacionais. 	

Introdução:
Como dito anteriormente, este trabalho busca analisar e comparar diferentes abordagens para a busca de palavras em textos, explorando o impacto do processamento serial e paralelo em sistemas computacionais.
Três métodos distintos foram implementados para realizar a contagem de ocorrências de uma palavra em arquivos de texto: 

- Serial CPU: Neste método foi implementado uma abordagem sequencial para percorrer cada linha de texto e contar as ocorrências de uma palavra. 
A execução é realizada em um único núcleo da CPU, o que o torna limitado em termos de escalabilidade para grandes volumes de dados

- Parallel CPU: Neste Método foi implementado uma abordagem paralela utilizando o conceito de multithreading. O texto é dividido em partes que são processadas simultaneamente por diferentes threads, cada uma contando as ocorrências da palavra em sua porção do texto.
Este método explora a capacidade de múltiplos núcleos da CPU, reduzindo o tempo de execução para grandes conjuntos de dados.

- Parallel GPU: Neste último método foi implementado o uso da OpenCL para paralelizar o processamento em uma GPU. A GPU é projetada para executar milhares de threads simultaneamente, o que a torna ideal para tarefas intensivas como a busca de palavras em grandes textos.
Apesar de oferecer um alto desempenho para tarefas de grande escala, o método apresenta desafios como o custo adicional de configuração e comunicação entre a CPU e a GPU.

Para realizar os testes, utilizamos três textos fornecidos pelo professor, que apresentavam diferentes tamanhos e níveis de complexidade, além de um texto mais simples, criado especificamente para validar a implementação. 
Os resultados de cada execução, incluindo o tempo de processamento e a quantidade de palavras encontradas, foram registrados em arquivos CSV. Esses dados foram então utilizados para gerar gráficos

Metodologia:
Sobre a construção deste trabalho, foram implementados três métodos de busca em texto, todos em Java.
Na classe TextSearch foram implementados os métodos de busca Serial, Paralelo CPU e paralelo GPU. Sobre estes métodos, o método SerialCPU utilizou uma abordagem sequencial simples para iterar sobre o texto e contar as ocorrências da palavra desejada. 
O método ParallelCPU, por sua vez, fez uso de multithreading através da API ForkJoinPool, que permitiu dividir o texto em partes menores, processadas simultaneamente por diferentes threads. 
Essa abordagem explorou os múltiplos núcleos da CPU, otimizando o desempenho ao consolidar os resultados após o processamento paralelo.
Já o método ParallelGPU empregou a biblioteca JOCL colocada na pasta lib e configurando na parte de  preferences do VScode para criar um kernel em OpenCL, permitindo a execução paralela em GPU. 
Este último método exigiu atenção maior à configuração do ambiente para garantir compatibilidade com os recursos de hardware disponíveis.

Antes de realizar o método ParallelGPU, foi necessário configurar o ambiente de modo a instalar e configurar a biblioteca JOCL, que oferece bindings Java para o OpenCL.
Em seguida, verificou-se a compatibilidade do notebook utilizado com o OpenCL, o que exigiu a atualização dos drivers da GPU. 
Para validar o suporte ao OpenCL  foi desenvolvida uma classe que verificava as plataformas disponíveis e confirmava se os drivers estavam corretamente configurados. Esse processo foi crucial para habilitar o uso da GPU no método ParallelGPU.

Desta forma, para o 3 método do trabalho foi a  criação do kernel OpenCL, que definiu a lógica de processamento paralelo. O kernel foi configurado para receber os dados de entrada, como o texto e a palavra a ser buscada, por meio de buffers de memória. 
Além disso, foi ajustado o tamanho global do trabalho (globalWorkSize) para garantir que cada posição relevante do texto fosse processada corretamente.
Outro ponto importante foi o tratamento de diferenças de codificação e case sensitivity, assegurando que a comparação entre texto e palavra ocorresse de forma consistente.

Diante disso, a classe Main foi projetada para receber como entrada o nome do arquivo de texto e a palavra a ser buscada. 
Após o processamento, os resultados, incluindo o tempo de execução e a quantidade de palavras encontradas, são coletados e armazenados em um arquivo CSV, utilizando a classe CsvWriter e após isso esses resultados são salvos no arquivo results.csv.
Para Implementar o gráfico, foi utilizado a classe ChartDisplay, desenvolvida para criar um gráfico de barras utilizando a biblioteca Java Swing. Essa classe leu os dados do arquivo CSV e gerou um gráfico.

Resultados e Discussão:
- Utilizando o texto Sample.txt e procurando pela palavra “java” este foi o Resultado que obtemos: 
SerialCPU: 10 occurrences in 46 ms
ParallelCPU: 10 occurrences in 11 ms
Build log: 
ParallelGPU: 10 occurrences in 129 ms
- Utilizando o texto DonQuixote-388208.txt e procurando pela palavra “Donde” este foi o Resultado que obtemos: 
SerialCPU: 722 occurrences in 247 ms
ParallelCPU: 722 occurrences in 116 ms
Build log: 
ParallelGPU: 846 occurrences in 200 ms
- Utilizando o texto MobyDick-217452.txt e procurando pela palavra “There” este foi o Resultado que obtemos:
SerialCPU: 865 occurrences in 191 ms
ParallelCPU: 865 occurrences in 72 ms
Build log: 
ParallelGPU: 983 occurrences in 169 ms
- Utilizando o texto Dracula-165307.txt e procurando pela palavra “The” este foi o Resultado que obtemos:
SerialCPU: 8101 occurrences in 149 ms
ParallelCPU: 8101 occurrences in 66 ms
Build log: 
ParallelGPU: 11608 occurrences in 167 ms
Diante destes resultados obtidos, podemos ver que o método ParallelCPU se mostrou mais constante e eficiente em relação ao SerialCPU, aproveitando o paralelismo na CPU para reduzir os tempos de execução.
Por outro lado, o método ParallelGPU demonstrou ser competitivo em textos maiores, mas apresentou limitações em cenários de menor escala.
O tempo necessário para configurar os buffers de memória e transferir dados entre CPU e GPU impactou negativamente sua performance em textos pequenos e médios.
Além disso, a análise também evidenciou o papel do tamanho e da complexidade do texto nos tempos de execução. Textos maiores se beneficiam das abordagens paralelas, enquanto textos pequenos nem tanto.

Conclusão:
A partir dos resultados obtidos neste trabalho, foi possível compreender como diferentes abordagens de busca em texto se comportam em termos de desempenho, considerando o processamento sequencial e paralelo em CPU e GPU.
Cada método apresentou vantagens e limitações, dependendo do tamanho e da complexidade dos textos processados.
O método SerialCPU, embora simples e direto, mostrou-se significativamente mais lento em comparação com as abordagens paralelas, especialmente para textos maiores. 
Ele é adequado para casos de baixa complexidade e onde os recursos computacionais disponíveis são limitados.
O método ParallelCPU, utilizando a API ForkJoinPool, destacou-se como a solução mais eficiente na maioria dos cenários. 
A divisão do texto em partes menores e o uso de múltiplos threads na CPU proporcionaram uma redução expressiva nos tempos de execução, demonstrando que essa abordagem é altamente eficaz para textos pequenos, médios e grandes.
Já o método ParallelGPU, implementado com a biblioteca JOCL e OpenCL, apresentou resultados bons em textos maiores, mas foi prejudicado pelo overhead de configuração e transferência de dados entre CPU e GPU, que impactaram negativamente seu desempenho em textos menores.
No entanto, sua capacidade de executar milhares de threads simultaneamente sugere que, para volumes extremamente grandes de dados, essa abordagem pode superar as demais.

Referências:



