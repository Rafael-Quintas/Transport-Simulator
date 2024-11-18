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
## Mockup da Interface Gráfica

### Visão Geral
A interface gráfica apresenta um mapa interativo que permite:
1. Visualizar a rede de transportes (paragens e rotas) geograficamente.
2. Selecionar paragens ou rotas para visualizar informações detalhadas.
3. Adicionar, editar ou remover paragens e rotas (possível implementação futura).

### Componentes Visuais
1. **Barra de Título**:
   - Exibe o nome do projeto: `Projeto PA 2024/25 - Maps`.
   

2. **Mapa Interativo**:
   - Mostra as paragens como círculos azuis.
   - Linhas tracejadas vermelhas indicam as rotas entre as paragens.
   

3. **Paragens e Rotas**:
   - Cada círculo azul representa uma paragem, identificado pelo nome.
   - As linhas tracejadas conectam as paragens, representando as rotas disponíveis.

---

## Mockup Visual
Exemplo de como a interface está estruturada:

![Mockup da Interface](\images\app_javafx.png)

---

# Mockup da Futura Interface Gráfica (GUI)
Descrição do mockup idealizado para a interface gráfica:

## **Tela Principal**

### Menu Superior
- **Opções principais:**
   - `Importar Dataset`: Permite carregar os arquivos CSV do dataset.
   - `Visualizar Métricas`: Exibe informações detalhadas, como número total de paragens, rotas e métricas de centralidade.
   - `Calcular Rota`: Acede à funcionalidade de cálculo do percurso mais curto com base em critérios como distância, tempo ou custo.
   - `Selecionar Percurso`: Ativa o modo de seleção manual de percursos.
   - `Reiniciar`: Reinicia a aplicação.
   - `Salvar/Exportar`: Permite guardar o estado atual ou exportar resultados.

### Área de Visualização do Grafo
- Representação gráfica do sistema de transportes, utilizando a biblioteca *JavaFXSmartGraph*.
- **Interatividade:**
   - Duplo clique numa paragem ou rota para obter informações detalhadas.
   - Destaque visual dos percursos calculados com cores associadas ao meio de transporte.

### Legenda
- Representação visual das paragens e rotas:
   - **Cores distintas:** Para os diferentes meios de transporte.
   - **Ícones:** Para diferenciar tipos de paragens (isoladas ou conectadas).

### **Métricas**
- **Botão ou Aba "Exibir Métricas"**:
   - Número total de paragens e rotas.
   - Lista de centralidade das paragens.
   - Gráfico de barras para as 5 paragens mais centrais.


### **Procura de Caminhos**
- **Formulário para entrada de dados:**
   - Paragem de origem e destino (_dropdown_ ou clique no grafo).
   - Critério de otimização: distância, duração ou sustentabilidade.
   - Meios de transporte selecionáveis (checkboxes para comboio, autocarro, barco, caminhada e bicicleta).
- **Botão "Calcular Caminho"**:
   - Atualiza o grafo com o percurso otimizado destacado.


### **Interatividade e Seleção Manual**
- **Modo Seleção Manual**:
   - Clique na paragem inicial.
   - Seleção de paragens subsequentes, apenas adjacentes.
   - Visualização imediata dos custos da rota escolhida.


### **Logger**
- Painel ou botão acessível para visualizar o histórico de operações realizadas.

---

## Estrutura dos Dados de Entrada

Os dados são carregados a partir de ficheiros CSV:
1. **`stops.csv`:** Contém as informações das paragens:
   - Código da Paragem, Nome, Latitude, Longitude.
   

2. **`routes.csv`:** Contém as conexões entre paragens:
   - Paragem de Origem, paragem de Destino, e os dados das rotas (distância, duração e custo por tipo de transporte).


3. **`xy.csv`:** Coordenadas para posicionar visualmente as paragens no mapa interativo.
