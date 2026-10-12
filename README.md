# Board de Tarefas em Java

Gerenciador de **boards de tarefas (estilo Kanban)** para o terminal, desenvolvido como desafio de
projeto da [DIO](https://www.dio.me) (*Criando um board de tarefas em Java*). O projeto passa por
planejamento, estruturação em camadas, persistência com JDBC, versionamento do banco com Liquibase e
regras de negócio.

## Funcionalidades

**Menu principal**
- Criar board (nome + colunas: *inicial*, quantas *pendentes* quiser, *final* e *cancelamento*)
- Selecionar board
- Excluir board (apaga colunas, cards e histórico junto)
- Listar boards

**Menu do board**
- Criar card (título e descrição; nasce na coluna inicial)
- Mover card para a próxima coluna
- Bloquear / desbloquear card (com motivo)
- Cancelar card
- Ver board (colunas e quantidade de cards), ver coluna com cards, ver card
- **Relatório do card:** tempo de permanência em cada coluna e histórico de bloqueios

## Regras de negócio

- Um board tem sempre uma coluna **inicial**, zero ou mais **pendentes**, uma **final** e uma de **cancelamento**,
  nessa ordem.
- O card só avança **para a coluna seguinte** (não pula etapas).
- Card **bloqueado** não pode ser movido nem cancelado até ser desbloqueado.
- Bloqueio e desbloqueio exigem um **motivo**.
- Card **finalizado** não pode ser movido, bloqueado nem cancelado. Card **cancelado** não pode ser movido.
- O cancelamento é possível a partir de qualquer coluna, exceto a final.
- Nomes de board são únicos.

## Arquitetura

```
src/main/java/br/com/dio/board/
├── Main.java                  # roda as migrations e abre o menu
├── config/                    # conexão (ConnectionConfig), migrations (MigrationRunner), transações
├── persistence/
│   ├── entity/                # BoardEntity, BoardColumnEntity, CardEntity, BoardColumnKindEnum
│   └── dao/                   # acesso ao banco com JDBC puro (um DAO por tabela)
├── service/                   # regras de negócio: BoardService, CardService, BoardQueryService
├── dto/                       # objetos de leitura (records)
├── exception/                 # exceções de negócio e de banco
└── ui/                        # menus do terminal (MainMenu, BoardMenu)

src/main/resources/db/changelog/   # migrations do Liquibase (tabelas boards, columns, cards, blocks, history)
```

Camadas: **ui → service → persistence**. A interface não conhece SQL e os DAOs não conhecem regras de negócio.
Cada operação do serviço roda em uma **transação** (commit ao final, rollback em caso de erro).

## Tecnologias

- Java 17+ (testado com o JDK 25)
- Gradle (wrapper incluso, não precisa instalar)
- JDBC
- Liquibase 4.29 (migrations)
- H2 (banco embutido, padrão) ou MySQL (opcional)

## Como executar

Requisito: **JDK 17 ou superior** (`javac -version`). Não é preciso instalar banco de dados nem Gradle.

```bash
# Linux/macOS/Git Bash
./gradlew installDist
./build/install/board-java/bin/board-java
```

```powershell
# Windows (PowerShell)
.\gradlew.bat installDist
.\build\install\board-java\bin\board-java.bat
```

A primeira execução baixa o Gradle e as dependências (precisa de internet). Também é possível usar
`./gradlew run --console=plain -q`.

Os dados ficam na pasta `data/` (H2), criada automaticamente na primeira execução. Para começar do zero,
apague essa pasta.

### Usando MySQL (opcional)

Crie um banco vazio e defina as variáveis de ambiente antes de executar:

```bash
export BOARD_DB_URL="jdbc:mysql://localhost:3306/board"
export BOARD_DB_USER="root"
export BOARD_DB_PASSWORD="sua_senha"
```

As tabelas são criadas automaticamente pelo Liquibase.

## Modelo de dados

`boards` 1─N `boards_columns` 1─N `cards` 1─N `blocks`, além de `card_column_history`
(entrada e saída do card em cada coluna). Todas as chaves estrangeiras usam `ON DELETE CASCADE`.
