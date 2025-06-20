CREATE TABLE users (
                       user_id     CHAR(36) PRIMARY KEY,
                       username    VARCHAR(100) UNIQUE NOT NULL,
                       full_name   VARCHAR(100)        NOT NULL,
                       password    VARCHAR(500)        NOT NULL,
                       email       VARCHAR(100),
                       role        ENUM('ADMIN', 'USER') DEFAULT 'USER',
                       created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE customers (
                           customer_id    CHAR(36) PRIMARY KEY,
                           nic_passport   VARCHAR(50) UNIQUE NOT NULL,
                           full_name      VARCHAR(100)       NOT NULL,
                           dob            DATE               NOT NULL,
                           address        TEXT               NOT NULL,
                           mobile_no      VARCHAR(15)        NOT NULL,
                           email          VARCHAR(100),
                           created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           INDEX idx_customer_name (full_name),
                           INDEX idx_nic (nic_passport)
);

CREATE TABLE saving_accounts (
                                 account_number VARCHAR(20) PRIMARY KEY,
                                 customer_id CHAR(36) NOT NULL,
                                 opening_date DATETIME NOT NULL,
                                 account_type ENUM('SAVING','PREMIUM','CHILDREN','WOMEN','SENIOR_CITIZEN') DEFAULT 'SAVING',
                                 balance DECIMAL(15,2) DEFAULT 0.00 NOT NULL CHECK (balance >= 0),
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);


CREATE TABLE transactions (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              account_number VARCHAR(20) NOT NULL,
                              amount DECIMAL(12,2) NOT NULL,
                              new_balance DECIMAL(12,2) NOT NULL,
                              transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'FEE', 'INTEREST') NOT NULL,
                              description VARCHAR(255),
                              reference_number VARCHAR(50),
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (account_number) REFERENCES saving_accounts(account_number) ON DELETE CASCADE,
                              INDEX idx_account_number (account_number),
                              INDEX idx_created_at (created_at)
);

CREATE TABLE audit_logs (
                            log_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                            actor_user_id  CHAR(36)                         NOT NULL,
                            action_type    ENUM('CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT') NOT NULL,
                            entity_type    ENUM('CUSTOMER', 'ACCOUNT', 'TRANSACTION', 'USER')    NOT NULL,
                            entity_id      CHAR(36),
                            description    TEXT,
                            ip_address     VARCHAR(45),
                            created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                            INDEX idx_actor_user_id (actor_user_id),
                            INDEX idx_entity_type (entity_type),
                            INDEX idx_created_at (created_at)
);

