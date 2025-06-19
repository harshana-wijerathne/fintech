INSERT INTO users (user_id, username, full_name, password, email, role)
VALUES
    (UUID(), 'admin', 'System Administrator', 'admin123', 'admin@example.com', 'ADMIN');

INSERT INTO customers (customer_id, nic_passport, full_name, dob, address, mobile_no, email) VALUES
                                                                                                 ('a4f98059-1d41-4389-a84e-6d76af01c525', 'NIC582127', 'Customer_80', '1989-01-02', 'Address_73', '0725656072', 'customer_80@example.com'),
                                                                                                 ('0ba0a082-879b-4bed-9ea2-b077b8af062a', 'NIC509345', 'Customer_70', '1999-03-16', 'Address_51', '0741733932', 'customer_70@example.com'),
                                                                                                 ('0e0ca4dc-cc6f-45c0-8ba4-297521f7c8d1', 'NIC183265', 'Customer_61', '1964-08-06', 'Address_13', '0784950180', 'customer_61@example.com'),
                                                                                                 ('53d6c8f4-c24f-44bf-b0f1-9c776d790664', 'NIC477543', 'Customer_35', '2004-05-28', 'Address_56', '0725477020', 'customer_35@example.com'),
                                                                                                 ('f030087d-6ae4-4a39-8978-6d906c212981', 'NIC501479', 'Customer_8', '1979-08-25', 'Address_37', '0760143871', 'customer_8@example.com'),
                                                                                                 ('050aff2b-f055-4fb0-a075-8c52b01ddcb8', 'NIC443532', 'Customer_55', '1989-04-03', 'Address_83', '0731955979', 'customer_55@example.com'),
                                                                                                 ('3f992f0a-0054-4af0-8874-1d74abb271d2', 'NIC456305', 'Customer_79', '1963-01-31', 'Address_12', '0779138659', 'customer_79@example.com'),
                                                                                                 ('04e97dcd-4cb0-461e-8d01-08f4f19d340a', 'NIC712156', 'Customer_95', '2001-01-02', 'Address_22', '0767907406', 'customer_95@example.com'),
                                                                                                 ('c74a5430-82d7-4082-a221-3cb427d5add1', 'NIC812151', 'Customer_99', '1968-06-28', 'Address_80', '0711671380', 'customer_99@example.com'),
                                                                                                 ('91e156df-b330-40f9-9818-32cdc6adaf13', 'NIC698898', 'Customer_99', '2001-11-26', 'Address_15', '0776133518', 'customer_99@example.com');


-- First insert the customer records
INSERT INTO saving_accounts (account_number, customer_id, opening_date, account_type, balance) VALUES
                                                                                                   ('d27e7cf5-e106-46e3-9', 'a4f98059-1d41-4389-a84e-6d76af01c525', '2022-01-23 00:00:00', 'PREMIUM', 7206.50),
                                                                                                   ('66ca6eaf-58e0-4568-8', '0ba0a082-879b-4bed-9ea2-b077b8af062a', '2023-12-28 00:00:00', 'CHILDREN', 6028.63),
                                                                                                   ('8dd6eba7-d10a-41e7-9', '0e0ca4dc-cc6f-45c0-8ba4-297521f7c8d1', '2021-12-18 00:00:00', 'CHILDREN', 9208.64),
                                                                                                   ('bd969e13-59c8-445a-8', '53d6c8f4-c24f-44bf-b0f1-9c776d790664', '2020-02-02 00:00:00', 'PREMIUM', 4583.28),
                                                                                                   ('61d8dadb-0893-46d0-a', 'f030087d-6ae4-4a39-8978-6d906c212981', '2023-10-13 00:00:00', 'WOMEN', 3327.95);

-- Then insert the savings accounts
INSERT INTO transactions (transaction_id, account_number, transaction_date, transaction_type, amount, balance_after) VALUES
                                                                                                                         ('6cb98aa0-9efa-4680-a6be-ee971bf3c3d0', 'd27e7cf5-e106-46e3-9', NOW(), 'DEPOSIT', 631.66, 7838.16),
                                                                                                                         ('fb1e7959-87b3-4a3f-bbc8-755c66693014', '66ca6eaf-58e0-4568-8', NOW(), 'WITHDRAW', 815.28, 5213.35),
                                                                                                                         ('3a2b1c00-db19-438d-ac73-aebc22ba47ee', '8dd6eba7-d10a-41e7-9', NOW(), 'DEPOSIT', 710.57, 9919.21),
                                                                                                                         ('66c45adc-d07f-4de7-9931-b426cec4b8f3', 'bd969e13-59c8-445a-8', NOW(), 'DEPOSIT', 643.04, 5226.32),
                                                                                                                         ('f291b931-2c8f-4175-b6e6-15bb617956db', '61d8dadb-0893-46d0-a', NOW(), 'DEPOSIT', 194.58, 3522.53);
