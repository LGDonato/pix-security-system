# Pix Security System

Projeto de backend e engenharia de dados simulando um sistema de segurança para transações PIX.

## Objetivo

Construir uma solução inspirada em ambientes bancários, com:

- Backend em Java/Spring Boot
- MySQL para dados relacionais de clientes
- MongoDB para vínculos seguros, limites e auditoria
- API de validação de risco PIX
- Pipeline de dados em Python
- Arquitetura Medallion: Bronze, Silver e Gold
- Orquestração com Airflow
- Dashboard analítico com Power BI

## Arquitetura

O projeto é dividido em dois blocos principais:

### Backend

Responsável por simular regras transacionais de segurança PIX.

### Data Layer

Responsável por ingerir, tratar e transformar os dados do backend em indicadores analíticos.

## Stack

- Java 21
- Spring Boot
- MySQL
- MongoDB
- Docker
- Python
- Pandas / PySpark
- Airflow
- Parquet
- Power BI

## Roadmap

- [ ] Estrutura inicial do repositório
- [ ] Docker Compose com MySQL e MongoDB
- [ ] Backend Spring Boot
- [ ] CRUD de clientes
- [ ] Módulo de segurança PIX
- [ ] API de validação de risco
- [ ] Testes automatizados
- [ ] Simulação de transações PIX
- [ ] Pipeline Bronze, Silver e Gold
- [ ] DAGs no Airflow
- [ ] Dashboard analítico
- [ ] Documentação final para portfólio
