# 📝 Post-it App

Aplicacao de notas adesivas (Post-it) desenvolvida em Java Swing com suporte a multiplas notas independentes, formatacao de texto rica e persistencia de dados.

---

## ✨ Funcionalidades

### 🎯 Funcionalidades Principais
- **Multiplas Notas**: Crie e gerencie varias notas simultaneamente
- **Posicionamento Livre**: Arraste as notas para qualquer posicao na tela
- **Redimensionamento**: Ajuste o tamanho das notas pelas bordas e cantos
- **Persistencia**: Todas as notas sao salvas automaticamente em `notes.json`

### 🌈 Cores e Personalizacao
- **Cores do Arco-Iris**: 7 cores pastel aleatorias para cada nota nova
  - Vermelho claro (`#FFB3BA`)
  - Laranja claro (`#FFDFBA`)
  - Amarelo claro (`#FFFFBA`)
  - Verde claro (`#BAFFC9`)
  - Azul claro (`#BAE1FF`)
  - Anil claro (`#BABAFF`)
  - Violeta claro (`#E1BAFF`)
- **Cor Personalizada**: Escolha qualquer cor via seletor de cores

### ✏️ Edicao de Texto
- **Texto Rico**: Suporte a HTML com formatacao preservada
- **Quebras de Linha**: Enter cria `<br>` para melhor preservacao
- **Formatacao Seletiva**: Aplique formatacao em partes do texto

### 🎨 Formatacao de Texto
- **Cor do Texto**: Escolha qualquer cor para o texto selecionado
- **Negrito**: Aplique/remova negrito no texto selecionado
- **Tamanho da Fonte**: Aumente ou diminua o tamanho (8pt a 48pt)

### 📌 Gerenciamento de Notas
- **Pin (Fixar)**: Mantenha a nota sempre visivel (always on top)
- **Nova Nota (➕)**: Crie notas com cor aleatoria do arco-iris
- **Salvar (💾)**: Salve manualmente todas as notas
- **Deletar (🗑️)**: Remova notas com confirmacao
- **Fechar (X)**: Feche a nota atual

---

## 🚀 Como Usar

### Iniciar a Aplicacao
```bash
mvn compile exec:java
```

### Criar uma Nova Nota
1. Clique no botao **➕** na barra superior
2. Uma nova nota aparecera com cor aleatoria do arco-iris
3. Tamanho padrao: **334 x 347 pixels**

### Formatar Texto
1. **Selecione** o texto desejado
2. Use o menu **Editar** para:
   - Escolher cor do texto
   - Aplicar negrito
   - Alterar tamanho da fonte

### Mover e Redimensionar
- **Arrastar**: Clique e arraste na barra superior
- **Redimensionar**: Passe o mouse nas bordas/cantos (cursor muda)
  - **Bordas**: Redimensiona uma direcao
  - **Cantos**: Redimensiona diagonalmente
  - **Minimo**: 150 x 150 pixels

---

## 🏗️ Arquitetura

### Estrutura do Projeto
```
Post-it/
├── pom.xml                 # Configuracao Maven
├── README.md              # Este arquivo
├── notes.json             # Dados persistidos
└── src/
    └── main/
        └── java/
            └── PostItApp.java   # Codigo fonte principal
```

### Tecnologias Utilizadas
- **Java Swing**: Interface grafica
- **JTextPane + HTMLDocument**: Edicao de texto rica
- **Gson**: Serializacao JSON para persistencia
- **Maven**: Gerenciamento de dependencias

---

## 🔧 Compilacao e Execucao

### Requisitos
- Java 8 ou superior
- Maven 3.x

### Compilar e Executar
```bash
mvn compile exec:java
```

---

## 🎨 Paleta de Cores

### Cores do Arco-Iris (Novas Notas)
| Cor | RGB | Hex |
|-----|-----|-----|
| Vermelho Claro | 255, 179, 186 | #FFB3BA |
| Laranja Claro | 255, 223, 186 | #FFDFBA |
| Amarelo Claro | 255, 255, 186 | #FFFFBA |
| Verde Claro | 186, 255, 201 | #BAFFC9 |
| Azul Claro | 186, 225, 255 | #BAE1FF |
| Anil Claro | 186, 186, 255 | #BABAFF |
| Violeta Claro | 225, 186, 255 | #E1BAFF |

---

## ✅ Funcionalidades Implementadas
- Multiplas notas independentes
- Cores aleatorias do arco-iris pastel
- Formatacao de texto rica (HTML)
- Texto colorido seletivo
- Negrito seletivo
- Tamanho de fonte ajustavel
- Persistencia completa (posicao, tamanho, formatacao)
- Redimensionamento em 8 direcoes
- Arraste pela barra superior
- Sistema de delecao com confirmacao
- Quebras de linha preservadas
