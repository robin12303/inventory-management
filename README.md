# Inventory Management System

Spring Boot 기반의 재고 관리(WMS) 백엔드 프로젝트입니다.

단순 CRUD 구현에 그치지 않고, 재고 입출고 과정에서 발생할 수 있는
동시성 문제와 데이터 정합성을 고려하고,
Docker 및 AWS 기반 CI/CD 환경까지 구축하는 것을 목표로 개발했습니다.

---

## 주요 기능

### 상품 관리
- 상품 등록
- 상품 단건/전체 조회
- 상품 수정
- 상품 삭제
- SKU 중복 검증

### 창고 관리
- 창고 등록
- 창고 단건/전체 조회
- 창고 수정
- 창고 삭제
- 창고 코드 중복 검증

### 재고 관리
- 재고 입고
- 재고 출고
- 창고 간 재고 이동
- 현재 재고 조회
- 재고 부족 검증
- 재고 변경 이력 저장

### 재고 이력
- 입고 / 출고 / 이동 이력 조회
- 창고별 필터링
- 상품별 필터링
- Pagination 지원

---

## Tech Stack

### Backend
- Java 17
- Spring Boot 4
- Spring Data JPA
- Hibernate
- Bean Validation

### Database
- MySQL 8.4
- Flyway

### Test
- JUnit
- Testcontainers
- MySQL Container

### Infrastructure
- AWS EC2
- AWS RDS MySQL
- AWS ECR
- AWS ALB
- AWS Route 53
- AWS Certificate Manager
- AWS Secrets Manager
- AWS Systems Manager (SSM)
- AWS IAM / GitHub OIDC

### CI/CD
- GitHub Actions
- Docker

---

## Architecture

```mermaid
flowchart TD
    U[Client] -->|HTTPS| R53[Route 53]
    R53 --> ALB[Application Load Balancer]
    ALB -->|HTTP :8080| EC2[EC2]

    EC2 --> APP[Spring Boot Docker Container]
    APP --> RDS[(RDS MySQL)]

    EC2 --> SM[Secrets Manager]

    GH[GitHub] --> GA[GitHub Actions]
    GA -->|OIDC| IAM[AWS IAM Role]
    IAM --> ECR[ECR]
    GA -->|SSM SendCommand| EC2
    ECR -->|Docker Pull| EC2
