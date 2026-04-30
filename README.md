# Pix Security System

Pix Security System é um projeto de backend e engenharia de dados que simula uma plataforma de segurança para transações PIX. A solução combina uma API transacional em Java/Spring Boot, um motor de risco para validação de operações PIX e um pipeline analítico em arquitetura Medallion, com camadas Bronze, Silver e Gold orquestradas pelo Apache Airflow.

O objetivo do projeto é demonstrar domínio de construção de APIs, modelagem de regras de negócio, persistência relacional e documental, processamento de dados em Python e organização de uma esteira analítica inspirada em ambientes financeiros.

## Visão Geral

O sistema foi desenhado para representar um fluxo comum em instituições financeiras digitais:

1. Clientes são cadastrados e mantidos pela API.
2. Limites PIX e vínculos confiáveis são configurados por cliente.
3. Uma transação PIX simulada é enviada ao motor de risco.
4. O motor avalia cliente, status cadastral, limites, horário da transação e dispositivo utilizado.
5. Eventos transacionais são gerados e processados no pipeline de dados.
6. A camada Gold produz indicadores prontos para consumo analítico e dashboards.

## Arquitetura

```text
pix-security-system
├── backend/pix-security-api
│   ├── API REST Spring Boot
│   ├── Módulo de clientes
│   ├── Módulo de limites PIX
│   ├── Módulo de vínculos confiáveis
│   └── Motor de risco PIX
│
├── database/mysql
│   └── Scripts SQL para estrutura relacional
│
├── data_pipeline
│   ├── generate_events.py
│   ├── bronze.py
│   ├── silver.py
│   └── gold.py
│
├── airflow/dags
│   └── pix_pipeline_dag.py
│
└── data
    ├── events
    ├── bronze
    ├── silver
    └── gold
```

### Componentes Principais

**API Backend**

Responsável pelas operações transacionais do sistema. A API expõe endpoints para cadastro de clientes, configuração de limites PIX, gerenciamento de vínculos confiáveis e validação de risco.

**Banco Relacional**

Utilizado para dados cadastrais de clientes, com estrutura inicial em MySQL.

**Banco Documental**

Utilizado para dados de segurança com estrutura mais flexível, como limites PIX e vínculos confiáveis de dispositivos, chaves ou contas.

**Motor de Risco**

Camada de decisão responsável por avaliar uma transação PIX simulada e retornar se ela foi aprovada, bloqueada ou classificada com risco baixo, médio ou alto.

**Pipeline de Dados**

Esteira em Python que gera eventos simulados, ingere dados brutos, realiza tratamento e produz tabelas analíticas em Parquet.

**Airflow**

Orquestra a execução do pipeline de dados de ponta a ponta, garantindo ordem de dependência entre geração de eventos, Bronze, Silver e Gold.

## Tecnologias

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Data MongoDB
- Bean Validation
- Springdoc OpenAPI / Swagger
- Maven
- JUnit 5
- Mockito
- AssertJ
- MySQL
- MongoDB
- Python
- Pandas
- PyArrow
- Apache Parquet
- Apache Airflow
- Docker Compose

## API e Domínio

A API está organizada por módulos de negócio:

| Módulo | Responsabilidade |
| --- | --- |
| Clientes | Cadastro, listagem, atualização e inativação de clientes |
| Limites PIX | Configuração de limite por transação, limite diário e limite noturno |
| Vínculos confiáveis | Registro de dispositivos, chaves ou contas confiáveis |
| Risco PIX | Validação de risco de uma transação PIX simulada |

Principais rotas:

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/customers` | Cria um cliente |
| `GET` | `/customers` | Lista clientes |
| `GET` | `/customers/{id}` | Busca cliente por ID |
| `PUT` | `/customers/{id}` | Atualiza dados do cliente |
| `DELETE` | `/customers/{id}` | Inativa cliente |
| `POST` | `/security/pix-limits` | Cria ou atualiza limites PIX |
| `GET` | `/security/pix-limits/customer/{customerId}` | Busca limites ativos do cliente |
| `POST` | `/security/trusted-bindings` | Cria vínculo confiável |
| `GET` | `/security/trusted-bindings/customer/{customerId}` | Lista vínculos por cliente |
| `DELETE` | `/security/trusted-bindings/{id}` | Remove vínculo confiável |
| `POST` | `/pix/risk/validate` | Valida o risco de uma transação PIX |

A documentação da API fica disponível via Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Motor de Risco PIX

O motor de risco é implementado no serviço `PixRiskService` e simula uma decisão antifraude baseada em regras objetivas.

### Fluxo de Validação

1. **Validação do cliente**
   - Verifica se o `customerId` possui formato válido.
   - Consulta o cliente cadastrado.
   - Bloqueia a transação se o cliente não existir ou estiver inativo.

2. **Validação de limites**
   - Verifica se o cliente possui limite PIX ativo.
   - Bloqueia a transação se não houver limite configurado.
   - Aplica regra específica para período noturno, entre 20h e 6h.
   - Reprova operações acima do limite por transação.

3. **Validação de dispositivo**
   - Consulta se o dispositivo informado está cadastrado como vínculo confiável.
   - Dispositivos não confiáveis não bloqueiam automaticamente a transação, mas elevam o risco para monitoramento.

### Classificações de Risco

| Nível | Resultado | Critério |
| --- | --- | --- |
| `LOW` | Aprovado | Cliente ativo, limite respeitado e dispositivo confiável |
| `MEDIUM` | Aprovado com monitoramento | Cliente e limite válidos, mas dispositivo não confiável |
| `HIGH` | Reprovado | Valor acima do limite por transação |
| `BLOCKED` | Bloqueado | Cliente inválido, inexistente, inativo, sem limite ou acima do limite noturno |

Essa abordagem demonstra separação clara entre regras de negócio, persistência e camada HTTP, facilitando evolução para modelos mais avançados, como score estatístico, machine learning ou integração com sistemas de antifraude externos.

## Pipeline de Dados

O pipeline segue a arquitetura Medallion, separando dados brutos, tratados e agregados.

### Geração de Eventos

O script `generate_events.py` simula uma fonte de eventos PIX. Ele gera registros em formato JSON Lines com campos como:

- `transactionId`
- `customerId`
- `amountCents`
- `deviceId`
- `transactionDateTime`
- `transactionType`
- `channel`

Esses eventos representam uma origem que poderia ser substituída futuramente por Kafka, filas, CDC ou integração direta com serviços transacionais.

### Bronze

A camada Bronze é responsável pela ingestão dos eventos brutos.

Entrada:

```text
data/events/pix_transactions.jsonl
```

Saída:

```text
data/bronze/pix_transactions.parquet
```

Características:

- Preserva os dados como chegam da origem.
- Converte JSON Lines para Parquet.
- Cria uma base eficiente para processamento posterior.

### Silver

A camada Silver realiza validação, limpeza e enriquecimento dos dados.

Entrada:

```text
data/bronze/pix_transactions.parquet
```

Saída:

```text
data/silver/pix_transactions_silver.parquet
```

Transformações aplicadas:

- Validação de colunas obrigatórias.
- Remoção de registros inválidos ou incompletos.
- Conversão de valores monetários de centavos para reais.
- Conversão e normalização de data/hora.
- Identificação de transações noturnas.
- Criação da categoria de risco por valor transacionado.

### Gold

A camada Gold produz dados agregados e prontos para análise.

Entrada:

```text
data/silver/pix_transactions_silver.parquet
```

Saídas:

```text
data/gold/customer_metrics.parquet
data/gold/risk_distribution.parquet
data/gold/night_transactions.parquet
data/gold/device_usage.parquet
```

Indicadores gerados:

- Total de transações por cliente.
- Volume financeiro total por cliente.
- Ticket médio por cliente.
- Distribuição de risco.
- Volume de transações noturnas.
- Uso de dispositivos.

## Orquestração com Airflow

O projeto inclui uma DAG chamada `pix_security_pipeline`, responsável por executar o pipeline completo na ordem correta:

```text
generate_events >> bronze >> silver >> gold
```

A DAG utiliza `BashOperator` para executar os scripts Python dentro do container do Airflow. O ambiente é provisionado com Docker Compose e instala as dependências necessárias para processamento com `pandas` e `pyarrow`.

Configurações principais:

- Airflow `2.9.3`
- Executor sequencial
- Exemplos desabilitados
- DAGs mapeadas a partir de `airflow/dags`
- Scripts mapeados a partir de `data_pipeline`
- Dados mapeados a partir de `data`
- Interface web exposta na porta `8080`

## Execução do Projeto

### Pré-requisitos

- Java 21
- Maven ou Maven Wrapper
- Python 3.11+
- Docker e Docker Compose
- MySQL
- MongoDB

### Executar a API

Acesse o diretório da API:

```bash
cd backend/pix-security-api
```

Execute os testes:

```bash
./mvnw test
```

Inicie a aplicação:

```bash
./mvnw spring-boot:run
```

No Windows, use:

```bash
mvnw.cmd spring-boot:run
```

Após iniciar, acesse:

```text
http://localhost:8080/swagger-ui.html
```

### Executar o Pipeline Manualmente

Na raiz do projeto:

```bash
python data_pipeline/generate_events.py --count 100
python data_pipeline/bronze.py
python data_pipeline/silver.py
python data_pipeline/gold.py
```

Os arquivos processados serão gravados nas pastas `data/bronze`, `data/silver` e `data/gold`.

### Executar com Airflow

Na raiz do projeto:

```bash
docker compose -f docker-compose-airflow.yml up
```

Acesse a interface do Airflow:

```text
http://localhost:8080
```

Depois, localize a DAG `pix_security_pipeline` e execute o fluxo manualmente.

## Testes

O projeto possui testes unitários para o motor de risco PIX, cobrindo cenários como:

- Cliente ativo com dispositivo confiável.
- Dispositivo não confiável.
- Valor acima do limite por transação.
- Cliente inválido.
- Cliente inexistente.
- Cliente inativo.
- Limite PIX não configurado.
- Valor acima do limite noturno.

Esses testes garantem que as regras críticas de decisão sejam verificadas de forma isolada, sem necessidade de carregar todo o contexto do Spring.

## Próximos Passos

- Adicionar Docker Compose completo para API, MySQL e MongoDB.
- Criar profiles de configuração para ambiente local e containerizado.
- Persistir eventos reais da API para alimentar o pipeline automaticamente.
- Incluir autenticação e autorização com Spring Security.
- Implementar auditoria de decisões do motor de risco.
- Evoluir o motor de risco para considerar limite diário acumulado.
- Integrar mensageria com Kafka ou RabbitMQ.
- Criar dashboard em Power BI ou ferramenta BI equivalente.
- Adicionar testes de integração com Testcontainers.
- Publicar documentação técnica com exemplos de payloads e decisões de risco.

## Diferenciais Técnicos

- Separação clara entre API transacional e pipeline analítico.
- Uso de arquitetura Medallion para organização dos dados.
- Motor de risco com regras de negócio testáveis e extensíveis.
- Persistência híbrida com banco relacional e documental.
- Orquestração de dados com Airflow.
- Estrutura adequada para evolução para cenários reais de antifraude, observabilidade, auditoria e analytics.

## Autor

Projeto desenvolvido por Lucas Donato como estudo prático de backend, engenharia de dados e arquitetura aplicada a um domínio financeiro.
