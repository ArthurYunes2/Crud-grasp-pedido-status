# crud-grasp-pedido-status

Sistema de gestão de pedidos de feira livre implementado em Java puro,
aplicando os padrões GRASP com persistência em JSON local.

**Grupo G — Tema 4: Pedido / StatusPedido**

## Integrantes do grupo

> Arthur Yunes

---

## Pré-requisitos

- Java JDK 11 ou superior
- Sem dependências externas (sem Maven, sem frameworks)

## Como compilar e executar

### 1. Compilar

A partir da raiz do projeto:

```bash
# Criar pasta de saída
mkdir -p out

# Compilar todos os arquivos Java
find src -name "*.java" > fontes.txt
javac -d out @fontes.txt
```

### 2. Executar

```bash
java -cp out feira.graspcrud.Main
```

Os arquivos de dados são criados automaticamente em `data/` na pasta onde o comando for executado.

### Script rápido (Linux/macOS)

```bash
mkdir -p out && find src -name "*.java" > fontes.txt && javac -d out @fontes.txt && java -cp out feira.graspcrud.Main
```

---

## Estrutura do projeto

```
src/feira/graspcrud/
├── Main.java                              ← Bootstrap / Creator
├── controller/
│   └── PedidoController.java             ← Controller GRASP
├── domain/
│   ├── Pedido.java                        ← Information Expert
│   └── StatusPedido.java                  ← Information Expert
├── dto/
│   ├── PedidoRequest.java                 ← Dados de entrada
│   └── StatusPedidoRequest.java           ← Dados de entrada
├── exception/
│   └── RegraNegocioException.java         ← Exceção de domínio
├── repository/
│   ├── PedidoRepository.java              ← Interface (Protected Variations)
│   └── StatusPedidoRepository.java        ← Interface (Protected Variations)
├── repository/json/
│   ├── PedidoRepositoryJson.java          ← Pure Fabrication + Indirection
│   └── StatusPedidoRepositoryJson.java    ← Pure Fabrication + Indirection
├── service/
│   ├── PedidoService.java                 ← Low Coupling + High Cohesion
│   └── StatusPedidoService.java           ← Low Coupling + High Cohesion
└── util/
    └── JsonMini.java                      ← Pure Fabrication
```

---

## Padrões GRASP aplicados

| Padrão | Classe(s) | Como foi aplicado |
|---|---|---|
| **Information Expert** | `Pedido`, `StatusPedido` | Validações e regras de estado ficam nas próprias entidades. `Pedido.validar()` verifica nome e status; `StatusPedido.podeSucceder()` encapsula a regra de transição. |
| **Creator** | `Main`, `PedidoService`, `StatusPedidoService` | `Main` instancia e conecta todas as dependências. Os services instanciam entidades de domínio a partir dos DTOs, pois possuem todos os dados necessários. |
| **Controller** | `PedidoController` | Recebe as entradas do menu textual, converte em DTOs e delega aos serviços. Não contém regras de negócio. |
| **Low Coupling** | `PedidoService`, `StatusPedidoService` | Os serviços dependem das interfaces `PedidoRepository` e `StatusPedidoRepository`, nunca das implementações concretas JSON. |
| **High Cohesion** | Todas as classes | Cada classe tem responsabilidade única e claramente definida. Cada método do controller/service realiza exatamente um caso de uso. |
| **Pure Fabrication** | `JsonMini`, `PedidoRepositoryJson`, `StatusPedidoRepositoryJson` | Classes criadas para fins de infraestrutura, sem representar conceitos do domínio da feira livre. |
| **Indirection** | `PedidoRepositoryJson`, `StatusPedidoRepositoryJson` | Implementam as interfaces de repositório, servindo de intermediários entre serviços e sistema de arquivos. |
| **Protected Variations** | `PedidoRepository`, `StatusPedidoRepository` | As interfaces protegem o domínio de mudanças na persistência. Trocar JSON por outro mecanismo exige apenas nova implementação da interface. |

---

## Regras de negócio implementadas

### Gerais
- Nome do `Pedido` é obrigatório e deve ter ao menos 3 caracteres.
- `StatusPedido` é obrigatório no cadastro do pedido.
- Nome do `StatusPedido` é obrigatório, único e deve ter ao menos 3 caracteres.
- Não é permitido remover um `StatusPedido` em uso por algum pedido.

### Específicas do Tema 4
- `Pedido` registra a **data de criação** automaticamente no momento do cadastro.
- A transição de status é **progressiva**: o novo status deve ter ordem de transição maior que o atual.
- **Não é permitido retroceder** para um status de ordem menor ou igual ao atual.
- `StatusPedido` é uma **entidade cadastrável** com campo `ordemTransicao` que define a sequência permitida.

---

## Persistência

Os dados são salvos automaticamente em:

```
data/
  status-pedido.json   ← lista de StatusPedido
  pedidos.json         ← lista de Pedido (referência ao StatusPedido por id)
```

Ao iniciar, os arquivos são carregados automaticamente se existirem.

---

## Menu do sistema

```
1. Cadastrar StatusPedido
2. Listar StatusPedido
3. Atualizar StatusPedido
4. Remover StatusPedido
5. Cadastrar Pedido
6. Listar Pedidos
7. Buscar Pedido por ID
8. Atualizar Pedido
9. Avançar Status do Pedido
10. Remover Pedido
0. Sair
```
