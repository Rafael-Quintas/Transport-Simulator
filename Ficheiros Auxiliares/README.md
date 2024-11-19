# Projeto PA 2024/25 - Mapa de Transportes

## Resumo do Trabalho
Este projeto consiste no desenvolvimento de uma aplicação em Java para a simulação de uma rede de transportes, representada por um grafo. Neste contexto, as paragens correspondem aos vértices e as rotas às arestas do grafo. A aplicação inclui uma interface gráfica que apresenta um mapa interativo, permitindo a análise detalhada da rede de transportes e das conexões entre as paragens.

### Funcionalidades principais:
- Visualização de paragens e rotas num mapa geográfico interativo.
- Interação com os elementos da rede para explorar informações e percursos.
- Carregamento de dados a partir de ficheiros CSV, incluindo coordenadas das paragens e detalhes das conexões entre elas.

O projeto foi implementado em Java, utilizando bibliotecas como:
- **SmartGraph** para visualização de grafos.
- **OpenCSV** para leitura de dados a partir de ficheiros CSV.

---

## Estrutura de Dados
A rede de transportes foi modelada através de um grafo, onde:
- **Vértices**: Representam as paragens (objetos da classe Stop)-
  - Cada paragem possui um código único, um nome e coordenadas geográficas (latitude e longitude).
- **Arestas**: Representam as rotas entre as paragens.
  - Cada aresta contém uma lista de rotas (List<Route>), que indica os diferentes meios de transporte disponíveis.

A estrutura de dados baseia-se em:
- **`Graph<Stop, List<Route>>`**: Utilizando a implementação `GraphEdgeList`.
- **Classes principais:**
   - `Stop`: Representa uma paragem com atributos como nome, código, latitude e longitude.
   - `Route`: Representa uma rota entre duas paragens, com informações como o tipo de transporte, distância, duração e custo.
   - `GenericRoute`: Combina uma paragem de origem, uma paragem de destino e uma lista de rotas.
   - `TransportMap`: Gera o grafo e carrega os dados necessários.
   - `DataImporter`: Lê os ficheiros CSV para carregar paragens e rotas no grafo.

---


## Interface Atual
Exemplo de como a interface está estruturada:

![Interface Atual](\Ficheiros Auxiliares\images\app_javafx.png)

---

# Mockup da Futura Interface Gráfica (GUI)
Descrição do mockup idealizado para a interface gráfica:

![Mockup](\Ficheiros Auxiliares\images\Mockup.png)

## Funcionalidades Identificadas na Mockup

### Navegação Principal (Barra Superior)
Botões interativos disponíveis na barra de navegação:
- **Origin**: Dropdown menu para escolher a paragem de origem.
- **Destination**: Dropdown menu para escolher a paragem de destino.
- **Criteria**: Dropdown menu para escolher o critério de otimização do percurso.
- **Transport Type**: Dropdown menu para escolher o meio de transporte.
- **Calculate Cost**: Butão que calcula o custo total com base nos critérios selecionados.
- **Top 5**: Butão que faz apresentar as 5 paragens mais relevantes (com base em critérios como centralidade ou popularidade).
- **Stops N Routes Away**: Butão que irá fazer aparecer pop ups em que o utilizador seleciona as N rotas e a paragem E.
- **Logs**: Butão que acede ao histórico de operações realizadas pelo utilizador na aplicação, pois estas serão gravadas automaticamente.

### Funcionalidade ao clicar no butão "Top 5".
![Tabela Top5](\Ficheiros Auxiliares\images\Top5Central.png)  
### Funcionalidade ao dar duplo clique numa rota.
Nota: Estes dados não são baseados no projeto apenas servem como uma ideia geral de apresentação da tabela mencionada no enunciado.
![Double Click Route](\Ficheiros Auxiliares\images\DoubleClickRoute.png)
---

### Mapa Interativo
#### Representação Geográfica:
- Paragens apresentadas como círculos azuis, com etiquetas indicando os seus nomes.
- Rotas desenhadas como linhas tracejadas vermelhas, ligando as paragens correspondentes.

#### Possível Interatividade:
- Clicar numa paragem para obter informações detalhadas (como conexões ou rotas disponíveis).
- Seleção dinâmica de rotas e percursos diretamente no mapa.

---

### Ferramentas de Pesquisa (Painel Superior Direito)
Filtros disponíveis para personalizar a pesquisa:
- **Origin**: Permite selecionar uma paragem de origem.
- **Destination**: Permite selecionar uma paragem de destino.
- **Transport**: Filtro para escolher o tipo de transporte (autocarro, comboio, etc.).
- **Sort By**: Critério de ordenação, possivelmente para exibir percursos com base em distância, custo ou duração.

---

### Painel de Cálculo de Percurso (Inferior Esquerdo)
#### Botão "Calculate Route":
- Inicia o cálculo do percurso ideal entre a paragem de origem e a de destino selecionadas.

#### Exibição de Resultados:
- **Distance**: Apresenta a distância total do percurso calculado.
- **Duration**: Exibe a duração estimada para o percurso.
- **Cost**: Mostra o custo associado ao percurso, dependendo dos meios de transporte selecionados.

---

### Estrutura dos Dados de Entrada

Os dados são carregados a partir de ficheiros CSV:
1. **`stops.csv`:** Contém as informações das paragens:
   - Código da Paragem, Nome, Latitude, Longitude.
   

2. **`routes.csv`:** Contém as conexões entre paragens:
   - Paragem de Origem, paragem de Destino, e os dados das rotas (distância, duração e custo por tipo de transporte).


3. **`xy.csv`:** Coordenadas para posicionar visualmente as paragens no mapa interativo.

---

## Créditos
Este projeto foi desenvolvido por:
- **Rafael Quintas**
- **Rafael Pato**
- **Guilherme Pereira**