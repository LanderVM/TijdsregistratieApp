-- Inserting into 'customer' table
INSERT INTO customer (is_active, created_at, id, updated_at, company_name, name)
VALUES (1, GETDATE(), 1, GETDATE(), 'Proximus', 'Anne Dubois'),
       (1, GETDATE(), 2, GETDATE(), 'UCB Pharma', 'Louis Martin'),
       (1, GETDATE(), 3, GETDATE(), 'Colruyt Group', 'Sophie Peeters'),
       (1, GETDATE(), 4, GETDATE(), 'ING Belgium', 'Mark De Smet'),
       (1, GETDATE(), 5, GETDATE(), 'AB InBev', 'Elise Van Damme'),
       (1, GETDATE(), 6, GETDATE(), 'KBC Group', 'Thomas Verstraeten'),
       (1, GETDATE(), 7, GETDATE(), 'Solvay', 'Isabelle Janssens'),
       (1, GETDATE(), 8, GETDATE(), 'BNP Paribas Fortis', 'Gilles Lambert'),
       (1, GETDATE(), 9, GETDATE(), 'Umicore', 'Laura Maes'),
       (1, GETDATE(), 10, GETDATE(), 'Barco', 'Jeroen Claes'),
       (1, GETDATE(), 11, GETDATE(), 'Telenet', 'Caroline De Bruyn'),
       (1, GETDATE(), 12, GETDATE(), 'bpost', 'Olivier Jacobs'),
       (1, GETDATE(), 13, GETDATE(), 'Recticel', 'Hugo Verbeeck'),
       (1, GETDATE(), 14, GETDATE(), 'Deme Group', 'Charlotte Verhoeven'),
       (1, GETDATE(), 15, GETDATE(), 'Lotus Bakeries', 'Vincent De Vos'),
       (1, GETDATE(), 16, GETDATE(), 'Etex Group', 'Julie Pauwels');


-- Inserting into 'project' table
INSERT INTO project (id, is_active, created_at, updated_at, customer_id,
                     start_date, end_date, total_work_minutes,
                     name, description)
VALUES (1, 1, GETDATE(), GETDATE(), 1,
        DATEADD(DAY, -10, CONVERT(DATE, GETDATE())), NULL, 100,
        'Network Upgrade', 'Upgrading the internal network infrastructure for Proximus'),

       (2, 1, GETDATE(), GETDATE(), 1,
        DATEADD(DAY, -30, CONVERT(DATE, GETDATE())), DATEADD(DAY, 90, CONVERT(DATE, GETDATE())), NULL,
        'Data Center Consolidation', 'Consolidating multiple data centers into one for Proximus'),

       (3, 1, GETDATE(), GETDATE(), 1,
        DATEADD(DAY, -20, CONVERT(DATE, GETDATE())), NULL, 300,
        'VoIP Implementation', 'Implementing VoIP systems across Proximus offices'),

       (4, 1, GETDATE(), GETDATE(), 2,
        DATEADD(DAY, -25, CONVERT(DATE, GETDATE())), DATEADD(DAY, -1, CONVERT(DATE, GETDATE())), NULL,
        'Clinical Trial System', 'Developing a clinical trial management system for UCB Pharma'),

       (5, 1, GETDATE(), GETDATE(), 2,
        DATEADD(DAY, -40, CONVERT(DATE, GETDATE())), NULL, 7200,
        'Lab Automation', 'Automating laboratory processes at UCB Pharma'),

       (6, 1, GETDATE(), GETDATE(), 2,
        DATEADD(DAY, -60, CONVERT(DATE, GETDATE())), DATEADD(DAY, -10, CONVERT(DATE, GETDATE())), NULL,
        'Patient Portal', 'Creating a patient portal for UCB Pharma'),

       (7, 1, GETDATE(), GETDATE(), 3,
        DATEADD(DAY, -35, CONVERT(DATE, GETDATE())), NULL, 250,
        'Supply Chain Optimization', 'Optimizing the supply chain management at Colruyt Group'),

       (8, 1, GETDATE(), GETDATE(), 3,
        DATEADD(DAY, -45, CONVERT(DATE, GETDATE())), DATEADD(DAY, -3, CONVERT(DATE, GETDATE())), NULL,
        'E-commerce Platform', 'Developing an e-commerce platform for Colruyt Group'),

       (9, 1, GETDATE(), GETDATE(), 3,
        DATEADD(DAY, -50, CONVERT(DATE, GETDATE())), NULL, 500,
        'Inventory Management System', 'Implementing an inventory management system for Colruyt Group'),

       (10, 1, GETDATE(), GETDATE(), 4,
        DATEADD(DAY, -20, CONVERT(DATE, GETDATE())), NULL, 150,
        'Mobile Banking App', 'Developing a new mobile banking app for ING Belgium'),

       (11, 1, GETDATE(), GETDATE(), 4,
        DATEADD(DAY, -50, CONVERT(DATE, GETDATE())), DATEADD(DAY, -5, CONVERT(DATE, GETDATE())), NULL,
        'Security Upgrade', 'Upgrading the security systems for ING Belgium'),

       (12, 1, GETDATE(), GETDATE(), 4,
        DATEADD(DAY, -10, CONVERT(DATE, GETDATE())), NULL, 200,
        'Blockchain Integration', 'Integrating blockchain technology into banking operations for ING Belgium'),

       (13, 1, GETDATE(), GETDATE(), 5,
        DATEADD(DAY, -30, CONVERT(DATE, GETDATE())), DATEADD(DAY, -10, CONVERT(DATE, GETDATE())), NULL,
        'Brewery Automation', 'Automating brewing processes at AB InBev'),

       (14, 1, GETDATE(), GETDATE(), 5,
        DATEADD(DAY, -25, CONVERT(DATE, GETDATE())), NULL, 300,
        'Supply Chain Digitization', 'Digitizing the supply chain management at AB InBev'),

       (15, 1, GETDATE(), GETDATE(), 5,
        DATEADD(DAY, -40, CONVERT(DATE, GETDATE())), DATEADD(DAY, -1, CONVERT(DATE, GETDATE())), NULL,
        'Customer Loyalty Program', 'Developing a customer loyalty program for AB InBev'),

       (16, 1, GETDATE(), GETDATE(), 6,
        DATEADD(DAY, -60, CONVERT(DATE, GETDATE())), NULL, 400,
        'Insurance Claims System', 'Implementing a new insurance claims system for KBC Group'),

       (17, 1, GETDATE(), GETDATE(), 6,
        DATEADD(DAY, -50, CONVERT(DATE, GETDATE())), DATEADD(DAY, -5, CONVERT(DATE, GETDATE())), NULL,
        'Data Analytics Platform', 'Building a data analytics platform for KBC Group'),

       (18, 1, GETDATE(), GETDATE(), 6,
        DATEADD(DAY, -15, CONVERT(DATE, GETDATE())), NULL, 250,
        'Risk Management System', 'Developing a risk management system for KBC Group'),

       (19, 1, GETDATE(), GETDATE(), 7,
        DATEADD(DAY, -30, CONVERT(DATE, GETDATE())), NULL, 300,
        'Sustainability Initiative', 'Implementing sustainability practices for Solvay'),

       (20, 1, GETDATE(), GETDATE(), 7,
        DATEADD(DAY, -45, CONVERT(DATE, GETDATE())), DATEADD(DAY, -5, CONVERT(DATE, GETDATE())), NULL,
        'R&D Data Management', 'Developing a data management system for R&D at Solvay'),

       (21, 1, GETDATE(), GETDATE(), 7,
        DATEADD(DAY, -20, CONVERT(DATE, GETDATE())), NULL, 150,
        'ERP System Upgrade', 'Upgrading the ERP system for Solvay'),

       (22, 1, GETDATE(), GETDATE(), 8,
        DATEADD(DAY, -35, CONVERT(DATE, GETDATE())), DATEADD(DAY, -10, CONVERT(DATE, GETDATE())), NULL,
        'Digital Banking Platform', 'Developing a digital banking platform for BNP Paribas Fortis'),

       (23, 1, GETDATE(), GETDATE(), 8,
        DATEADD(DAY, -25, CONVERT(DATE, GETDATE())), NULL, 86400,
        'AI Customer Service', 'Implementing AI for customer service at BNP Paribas Fortis'),

       (24, 1, GETDATE(), GETDATE(), 8,
        DATEADD(DAY, -50, CONVERT(DATE, GETDATE())), DATEADD(DAY, -1, CONVERT(DATE, GETDATE())), NULL,
        'Fraud Detection System', 'Creating a fraud detection system for BNP Paribas Fortis'),

       (25, 1, GETDATE(), GETDATE(), 9,
        DATEADD(DAY, -40, CONVERT(DATE, GETDATE())), NULL, 300,
        'Sustainability Reporting', 'Developing sustainability reporting tools for Umicore'),

       (26, 1, GETDATE(), GETDATE(), 9,
        DATEADD(DAY, -30, CONVERT(DATE, GETDATE())), DATEADD(DAY, -10, CONVERT(DATE, GETDATE())), NULL,
        'Recycling Process Optimization', 'Optimizing recycling processes at Umicore'),

       (27, 1, GETDATE(), GETDATE(), 9,
        DATEADD(DAY, -20, CONVERT(DATE, GETDATE())), NULL, 150,
        'Supply Chain Transparency', 'Improving supply chain transparency for Umicore')

-- Inserting into 'tag' table
INSERT INTO tag (is_active, created_at, id, updated_at, name)
VALUES (1, GETDATE(), 1, GETDATE(), 'Mobile UI'),
       (1, GETDATE(), 2, GETDATE(), 'Website'),
       (1, GETDATE(), 3, GETDATE(), 'API'),
       (1, GETDATE(), 4, GETDATE(), 'Administration'),
       (1, GETDATE(), 5, GETDATE(), 'Backend Development'),
       (1, GETDATE(), 6, GETDATE(), 'Frontend Development'),
       (1, GETDATE(), 7, GETDATE(), 'Database'),
       (1, GETDATE(), 8, GETDATE(), 'Testing'),
       (1, GETDATE(), 9, GETDATE(), 'Security'),
       (1, GETDATE(), 10, GETDATE(), 'UI Design'),
       (1, GETDATE(), 11, GETDATE(), 'UX Design'),
       (1, GETDATE(), 12, GETDATE(), 'DevOps'),
       (1, GETDATE(), 13, GETDATE(), 'Cloud Computing'),
       (1, GETDATE(), 14, GETDATE(), 'Data Analysis'),
       (1, GETDATE(), 15, GETDATE(), 'Machine Learning'),
       (1, GETDATE(), 16, GETDATE(), 'Data Science'),
       (1, GETDATE(), 17, GETDATE(), 'Project Management'),
       (1, GETDATE(), 18, GETDATE(), 'Documentation'),
       (1, GETDATE(), 19, GETDATE(), 'User Experience'),
       (1, GETDATE(), 20, GETDATE(), 'Accessibility'),
       (1, GETDATE(), 21, GETDATE(), 'Agile'),
       (1, GETDATE(), 22, GETDATE(), 'Scrum'),
       (1, GETDATE(), 23, GETDATE(), 'Kanban'),
       (1, GETDATE(), 24, GETDATE(), 'Code Review'),
       (1, GETDATE(), 25, GETDATE(), 'Code Refactoring'),
       (1, GETDATE(), 26, GETDATE(), 'Continuous Integration'),
       (1, GETDATE(), 27, GETDATE(), 'Continuous Deployment'),
       (1, GETDATE(), 28, GETDATE(), 'Quality Assurance');

-- Inserting into 'task' table
INSERT INTO task (is_active, created_at, id, assignedProject_id, updated_at, name)
VALUES (1, GETDATE(), 1, 1, GETDATE(), 'Network Configuration'),
       (1, GETDATE(), 2, 1, GETDATE(), 'Hardware Installation'),
       (1, GETDATE(), 3, 1, GETDATE(), 'Testing and Debugging'),
       (1, GETDATE(), 4, 1, GETDATE(), 'Documentation'),
       (1, GETDATE(), 5, 1, GETDATE(), 'Client Training'),

       (1, GETDATE(), 6, 2, GETDATE(), 'Data Collection'),
       (1, GETDATE(), 7, 2, GETDATE(), 'Analysis and Interpretation'),
       (1, GETDATE(), 8, 2, GETDATE(), 'Report Writing'),
       (1, GETDATE(), 9, 2, GETDATE(), 'Presentation Preparation'),
       (1, GETDATE(), 10, 2, GETDATE(), 'Client Meeting'),

       (1, GETDATE(), 11, 3, GETDATE(), 'System Design'),
       (1, GETDATE(), 12, 3, GETDATE(), 'Development and Coding'),
       (1, GETDATE(), 13, 3, GETDATE(), 'Integration Testing'),
       (1, GETDATE(), 14, 3, GETDATE(), 'User Acceptance Testing'),
       (1, GETDATE(), 15, 3, GETDATE(), 'Deployment Planning'),

       (1, GETDATE(), 16, 4, GETDATE(), 'Mobile App UI Design'),
       (1, GETDATE(), 17, 4, GETDATE(), 'Back-end Development'),
       (1, GETDATE(), 18, 4, GETDATE(), 'API Integration'),
       (1, GETDATE(), 19, 4, GETDATE(), 'Testing and Debugging'),
       (1, GETDATE(), 20, 4, GETDATE(), 'User Feedback Implementation'),

       (1, GETDATE(), 21, 5, GETDATE(), 'Ingredient Sourcing'),
       (1, GETDATE(), 22, 5, GETDATE(), 'Brewing Process'),
       (1, GETDATE(), 23, 5, GETDATE(), 'Quality Control'),
       (1, GETDATE(), 24, 5, GETDATE(), 'Packaging'),
       (1, GETDATE(), 25, 5, GETDATE(), 'Distribution Planning'),

       (1, GETDATE(), 26, 6, GETDATE(), 'Insurance Claims Data Collection'),
       (1, GETDATE(), 27, 6, GETDATE(), 'Backend System Development'),
       (1, GETDATE(), 28, 6, GETDATE(), 'Frontend Interface Design'),
       (1, GETDATE(), 29, 6, GETDATE(), 'Testing and Debugging'),
       (1, GETDATE(), 30, 6, GETDATE(), 'User Training'),

       (1, GETDATE(), 31, 7, GETDATE(), 'Research on Sustainable Practices'),
       (1, GETDATE(), 32, 7, GETDATE(), 'Implementation of Green Initiatives'),
       (1, GETDATE(), 33, 7, GETDATE(), 'Monitoring and Reporting'),
       (1, GETDATE(), 34, 7, GETDATE(), 'Stakeholder Engagement'),
       (1, GETDATE(), 35, 7, GETDATE(), 'Employee Training'),

       (1, GETDATE(), 36, 8, GETDATE(), 'Requirements Gathering'),
       (1, GETDATE(), 37, 8, GETDATE(), 'Database Design'),
       (1, GETDATE(), 38, 8, GETDATE(), 'Frontend Development'),
       (1, GETDATE(), 39, 8, GETDATE(), 'Backend Development'),
       (1, GETDATE(), 40, 8, GETDATE(), 'Testing and Debugging'),

       (1, GETDATE(), 41, 9, GETDATE(), 'Metal Recycling Process Analysis'),
       (1, GETDATE(), 42, 9, GETDATE(), 'Waste Reduction Strategies'),
       (1, GETDATE(), 43, 9, GETDATE(), 'Supply Chain Optimization'),
       (1, GETDATE(), 44, 9, GETDATE(), 'Quality Control Measures'),
       (1, GETDATE(), 45, 9, GETDATE(), 'Environmental Impact Assessment'),

       (1, GETDATE(), 46, 10, GETDATE(), 'UI Design for Mobile App'),
       (1, GETDATE(), 47, 10, GETDATE(), 'Backend Development'),
       (1, GETDATE(), 48, 10, GETDATE(), 'Integration with Banking Systems'),
       (1, GETDATE(), 49, 10, GETDATE(), 'Testing and Debugging'),
       (1, GETDATE(), 50, 10, GETDATE(), 'User Acceptance Testing'),

       (1, GETDATE(), 51, 11, GETDATE(), 'AI Model Development'),
       (1, GETDATE(), 52, 11, GETDATE(), 'Integration with Customer Service Channels'),
       (1, GETDATE(), 53, 11, GETDATE(), 'Testing Natural Language Processing Algorithms'),
       (1, GETDATE(), 54, 11, GETDATE(), 'User Feedback Analysis'),
       (1, GETDATE(), 55, 11, GETDATE(), 'Deployment Planning'),

       (1, GETDATE(), 56, 12, GETDATE(), 'Fraud Detection Algorithm Design'),
       (1, GETDATE(), 57, 12, GETDATE(), 'Integration with Banking Systems'),
       (1, GETDATE(), 58, 12, GETDATE(), 'Testing and Debugging'),
       (1, GETDATE(), 59, 12, GETDATE(), 'User Acceptance Testing'),
       (1, GETDATE(), 60, 12, GETDATE(), 'Deployment to Production'),

       (1, GETDATE(), 61, 13, GETDATE(), 'Data Collection and Analysis'),
       (1, GETDATE(), 62, 13, GETDATE(), 'System Architecture Design'),
       (1, GETDATE(), 63, 13, GETDATE(), 'Database Implementation'),
       (1, GETDATE(), 64, 13, GETDATE(), 'Frontend Development'),
       (1, GETDATE(), 65, 13, GETDATE(), 'Backend Development'),

       (1, GETDATE(), 66, 14, GETDATE(), 'Mobile App Design and Development'),
       (1, GETDATE(), 67, 14, GETDATE(), 'Integration with Supply Chain Systems'),
       (1, GETDATE(), 68, 14, GETDATE(), 'Testing and Debugging'),
       (1, GETDATE(), 69, 14, GETDATE(), 'User Acceptance Testing'),
       (1, GETDATE(), 70, 14, GETDATE(), 'Deployment Planning'),

       (1, GETDATE(), 71, 15, GETDATE(), 'User Interface Design'),
       (1, GETDATE(), 72, 15, GETDATE(), 'Database Setup and Configuration'),
       (1, GETDATE(), 73, 15, GETDATE(), 'Frontend Development'),
       (1, GETDATE(), 74, 15, GETDATE(), 'Backend Development'),
       (1, GETDATE(), 75, 15, GETDATE(), 'Testing and Quality Assurance'),

       (1, GETDATE(), 76, 16, GETDATE(), 'Market Research and Analysis'),
       (1, GETDATE(), 77, 16, GETDATE(), 'Product Design and Prototyping'),
       (1, GETDATE(), 78, 16, GETDATE(), 'Manufacturing Process Optimization'),
       (1, GETDATE(), 79, 16, GETDATE(), 'Quality Control and Testing'),
       (1, GETDATE(), 80, 16, GETDATE(), 'Packaging and Distribution Planning'),

       (1, GETDATE(), 81, 17, GETDATE(), 'Customer Requirements Gathering'),
       (1, GETDATE(), 82, 17, GETDATE(), 'Database Design and Implementation'),
       (1, GETDATE(), 83, 17, GETDATE(), 'Frontend Development'),
       (1, GETDATE(), 84, 17, GETDATE(), 'Backend Development'),
       (1, GETDATE(), 85, 17, GETDATE(), 'Testing and Debugging'),

       (1, GETDATE(), 86, 18, GETDATE(), 'User Research and Analysis'),
       (1, GETDATE(), 87, 18, GETDATE(), 'UX/UI Design'),
       (1, GETDATE(), 88, 18, GETDATE(), 'Frontend Development'),
       (1, GETDATE(), 89, 18, GETDATE(), 'Backend Development'),
       (1, GETDATE(), 90, 18, GETDATE(), 'Testing and Quality Assurance'),

       (1, GETDATE(), 91, 19, GETDATE(), 'Requirements Gathering'),
       (1, GETDATE(), 92, 19, GETDATE(), 'System Design'),
       (1, GETDATE(), 93, 19, GETDATE(), 'Database Setup and Configuration'),
       (1, GETDATE(), 94, 19, GETDATE(), 'Frontend Development'),
       (1, GETDATE(), 95, 19, GETDATE(), 'Backend Development'),

       (1, GETDATE(), 96, 20, GETDATE(), 'Market Analysis'),
       (1, GETDATE(), 97, 20, GETDATE(), 'Product Development'),
       (1, GETDATE(), 98, 20, GETDATE(), 'Prototype Testing'),
       (1, GETDATE(), 99, 20, GETDATE(), 'Manufacturing Planning'),
       (1, GETDATE(), 100, 20, GETDATE(), 'Launch Strategy');

-- Inserting users
INSERT INTO account (id, is_active, created_at, updated_at, first_name, family_name)
VALUES (1, 1, GETDATE(), GETDATE(), 'Thomas', 'Blindeman'),
       (2, 1, GETDATE(), GETDATE(), 'Wietse', 'Renson'),
       (3, 1, GETDATE(), GETDATE(), 'Matteo', 'De Backer')

-- Assigning test Auth0 identities to users
INSERT INTO auth0_identity (auth0id, user_id)
VALUES ('auth0|661fa3fb0a9910987d67aee2', 1),
       ('auth0|661fac6b5ada15a729c28535', 2),
       ('auth0|661facd7985864790ec4801e', 3)

-- Inserting into 'time_registration' table
INSERT INTO time_registration (id, is_active, created_at, updated_at, start_time, end_time, assignedProject_id,
                               assignedTask_id, registrar_id, description)
VALUES
-- Monday, 4th of March
(1, 1, GETDATE(), GETDATE(), '2024-03-04 09:00:00', '2024-03-04 11:00:00', 1, 1, 1, 'Coding'),
(2, 1, GETDATE(), GETDATE(), '2024-03-04 12:00:00', '2024-03-04 13:00:00', 2, NULL, 1, 'Meeting with Team'),
(3, 1, GETDATE(), GETDATE(), '2024-03-04 14:00:00', '2024-03-04 16:00:00', 3, NULL, 1, 'Code Review'),

-- Tuesday, 5th of March
(4, 1, GETDATE(), GETDATE(), '2024-03-05 09:00:00', '2024-03-05 12:00:00', 1, 1, 1, 'Debugging'),
(5, 1, GETDATE(), GETDATE(), '2024-03-05 12:30:00', '2024-03-05 14:00:00', 2, 2, 1, 'UI Design'),
(6, 1, GETDATE(), GETDATE(), '2024-03-05 15:00:00', '2024-03-05 17:00:00', 3, 3, 1, 'Database Optimization'),

-- Wednesday, 6th of March
(7, 1, GETDATE(), GETDATE(), '2024-03-06 09:00:00', '2024-03-06 11:00:00', 1, 1, 1, 'Feature Implementation'),
(8, 1, GETDATE(), GETDATE(), '2024-03-06 12:00:00', '2024-03-06 14:00:00', 2, NULL, 1, 'Client Demo'),
(9, 1, GETDATE(), GETDATE(), '2024-03-06 14:30:00', '2024-03-06 17:00:00', 3, NULL, 1, 'Code Refactoring'),

-- Thursday, 7th of March
(10, 1, GETDATE(), GETDATE(), '2024-03-07 09:00:00', '2024-03-07 12:00:00', 1, 1, 1, 'Testing'),
(11, 1, GETDATE(), GETDATE(), '2024-03-07 13:00:00', '2024-03-07 15:00:00', 2, 2, 1, 'Bug Fixing'),
(12, 1, GETDATE(), GETDATE(), '2024-03-07 15:30:00', '2024-03-07 17:00:00', 3, 3, 1, 'Documentation'),

-- Friday, 8th of March
(13, 1, GETDATE(), GETDATE(), '2024-03-08 09:00:00', '2024-03-08 11:00:00', 1, 1, 1, 'Feature Testing'),
(14, 1, GETDATE(), GETDATE(), '2024-03-08 12:00:00', '2024-03-08 14:00:00', 2, NULL, 1, 'Internal Training'),
(15, 1, GETDATE(), GETDATE(), '2024-03-08 14:30:00', '2024-03-08 17:00:00', 3, NULL, 1, 'Deployment Preparation'),

-- Monday, 11th of March
(16, 1, GETDATE(), GETDATE(), '2024-03-11 09:00:00', '2024-03-11 12:00:00', 1, 1, 1, 'Feature Development'),
(17, 1, GETDATE(), GETDATE(), '2024-03-11 13:00:00', '2024-03-11 15:00:00', 2, NULL, 1, 'Client Meeting'),
(18, 1, GETDATE(), GETDATE(), '2024-03-11 15:30:00', '2024-03-11 17:00:00', 3, NULL, 1, 'Code Review'),

-- Tuesday, 12th of March
(19, 1, GETDATE(), GETDATE(), '2024-03-12 09:00:00', '2024-03-12 11:00:00', 1, 1, 1, 'Testing'),
(20, 1, GETDATE(), GETDATE(), '2024-03-12 12:00:00', '2024-03-12 14:00:00', 2, 2, 1, 'Bug Fixing'),
(21, 1, GETDATE(), GETDATE(), '2024-03-12 14:30:00', '2024-03-12 17:00:00', 3, 3, 1, 'Documentation'),

-- Wednesday, 13th of March
(22, 1, GETDATE(), GETDATE(), '2024-03-13 09:00:00', '2024-03-13 12:00:00', 1, 1, 1, 'Feature Implementation'),
(23, 1, GETDATE(), GETDATE(), '2024-03-13 12:30:00', '2024-03-13 14:00:00', 2, NULL, 1, 'Internal Meeting'),
(24, 1, GETDATE(), GETDATE(), '2024-03-13 15:00:00', '2024-03-13 17:00:00', 3, NULL, 1, 'Code Refactoring'),

-- Thursday, 14th of March
(25, 1, GETDATE(), GETDATE(), '2024-03-14 09:00:00', '2024-03-14 11:00:00', 1, 1, 1, 'Feature Testing'),
(26, 1, GETDATE(), GETDATE(), '2024-03-14 12:00:00', '2024-03-14 14:00:00', 2, 2, 1, 'Client Demo'),
(27, 1, GETDATE(), GETDATE(), '2024-03-14 14:30:00', '2024-03-14 17:00:00', 3, 3, 1, 'Deployment Preparation'),

-- Friday, 15th of March
(28, 1, GETDATE(), GETDATE(), '2024-03-15 09:00:00', '2024-03-15 12:00:00', 1, 1, 1, 'Sprint Planning'),
(29, 1, GETDATE(), GETDATE(), '2024-03-15 12:30:00', '2024-03-15 14:00:00', 2, NULL, 1, 'Code Review'),
(30, 1, GETDATE(), GETDATE(), '2024-03-15 15:00:00', '2024-03-15 17:00:00', 3, NULL, 1, 'Documentation'),

-- Monday, 25th of March
(31, 1, GETDATE(), GETDATE(), '2024-03-25 09:00:00', '2024-03-25 12:00:00', 1, 1, 1, 'Feature Development'),
(32, 1, GETDATE(), GETDATE(), '2024-03-25 13:00:00', '2024-03-25 15:00:00', 2, NULL, 1, 'Client Meeting'),
(33, 1, GETDATE(), GETDATE(), '2024-03-25 15:30:00', '2024-03-25 17:00:00', 3, NULL, 1, 'Code Review'),

-- Tuesday, 26th of March
(34, 1, GETDATE(), GETDATE(), '2024-03-26 09:00:00', '2024-03-26 11:00:00', 1, 1, 1, 'Testing'),
(35, 1, GETDATE(), GETDATE(), '2024-03-26 12:00:00', '2024-03-26 14:00:00', 2, 2, 1, 'Bug Fixing'),
(36, 1, GETDATE(), GETDATE(), '2024-03-26 14:30:00', '2024-03-26 17:00:00', 3, 3, 1, 'Documentation'),

-- Wednesday, 27th of March
(37, 1, GETDATE(), GETDATE(), '2024-03-27 09:00:00', '2024-03-27 12:00:00', 1, 1, 1, 'Feature Implementation'),
(38, 1, GETDATE(), GETDATE(), '2024-03-27 12:30:00', '2024-03-27 14:00:00', 2, NULL, 1, 'Internal Meeting'),
(39, 1, GETDATE(), GETDATE(), '2024-03-27 15:00:00', '2024-03-27 17:00:00', 3, NULL, 1, 'Code Refactoring'),

-- Thursday, 28th of March
(40, 1, GETDATE(), GETDATE(), '2024-03-28 09:00:00', '2024-03-28 11:00:00', 1, 1, 1, 'Feature Testing'),
(41, 1, GETDATE(), GETDATE(), '2024-03-28 12:00:00', '2024-03-28 14:00:00', 2, 2, 1, 'Client Demo'),
(42, 1, GETDATE(), GETDATE(), '2024-03-28 14:30:00', '2024-03-28 17:00:00', 3, 3, 1, 'Deployment Preparation'),

-- Friday, 29th of March
(43, 1, GETDATE(), GETDATE(), '2024-03-29 09:00:00', '2024-03-29 12:00:00', 1, 1, 1, 'Sprint Planning'),
(44, 1, GETDATE(), GETDATE(), '2024-03-29 12:30:00', '2024-03-29 14:00:00', 2, NULL, 1, 'Code Review'),
(45, 1, GETDATE(), GETDATE(), '2024-03-29 15:00:00', '2024-03-29 17:00:00', 3, NULL, 1, 'Documentation'),

-- Monday, 1st of April
(46, 1, GETDATE(), GETDATE(), '2024-04-01 09:00:00', '2024-04-01 12:00:00', 1, 1, 1, 'Feature Development'),
(47, 1, GETDATE(), GETDATE(), '2024-04-01 13:00:00', '2024-04-01 15:00:00', 2, NULL, 1, 'Client Meeting'),
(48, 1, GETDATE(), GETDATE(), '2024-04-01 15:30:00', '2024-04-01 17:00:00', 3, NULL, 1, 'Code Review'),

-- Tuesday, 2nd of April
(49, 1, GETDATE(), GETDATE(), '2024-04-02 09:00:00', '2024-04-02 11:00:00', 1, 1, 1, 'Testing'),
(50, 1, GETDATE(), GETDATE(), '2024-04-02 12:00:00', '2024-04-02 14:00:00', 2, 2, 1, 'Bug Fixing'),
(51, 1, GETDATE(), GETDATE(), '2024-04-02 14:30:00', '2024-04-02 17:00:00', 3, 3, 1, 'Documentation'),

-- Wednesday, 3rd of April
(52, 1, GETDATE(), GETDATE(), '2024-04-03 09:00:00', '2024-04-03 12:00:00', 1, 1, 1, 'Feature Implementation'),
(53, 1, GETDATE(), GETDATE(), '2024-04-03 12:30:00', '2024-04-03 14:00:00', 2, NULL, 1, 'Internal Meeting'),
(54, 1, GETDATE(), GETDATE(), '2024-04-03 15:00:00', '2024-04-03 17:00:00', 3, NULL, 1, 'Code Refactoring'),

-- Thursday, 4th of April
(55, 1, GETDATE(), GETDATE(), '2024-04-04 09:00:00', '2024-04-04 11:00:00', 1, 1, 1, 'Feature Testing'),
(56, 1, GETDATE(), GETDATE(), '2024-04-04 12:00:00', '2024-04-04 14:00:00', 2, 2, 1, 'Client Demo'),
(57, 1, GETDATE(), GETDATE(), '2024-04-04 14:30:00', '2024-04-04 17:00:00', 3, 3, 1, 'Deployment Preparation'),

-- Friday, 5th of April
(58, 1, GETDATE(), GETDATE(), '2024-04-05 09:00:00', '2024-04-05 12:00:00', 1, 1, 1, 'Sprint Planning'),
(59, 1, GETDATE(), GETDATE(), '2024-04-05 12:30:00', '2024-04-05 14:00:00', 2, NULL, 1, 'Code Review'),
(60, 1, GETDATE(), GETDATE(), '2024-04-05 15:00:00', '2024-04-05 17:00:00', 3, NULL, 1, 'Documentation'),

-- Monday, 8th of April
(61, 1, GETDATE(), GETDATE(), '2024-04-08 09:00:00', '2024-04-08 12:00:00', 1, 1, 1, 'Feature Development'),
(62, 1, GETDATE(), GETDATE(), '2024-04-08 13:00:00', '2024-04-08 15:00:00', 2, NULL, 1, 'Client Meeting'),
(63, 1, GETDATE(), GETDATE(), '2024-04-08 15:30:00', '2024-04-08 17:00:00', 3, NULL, 1, 'Code Review'),

-- Tuesday, 9th of April
(64, 1, GETDATE(), GETDATE(), '2024-04-09 09:00:00', '2024-04-09 11:00:00', 1, 1, 1, 'Testing'),
(65, 1, GETDATE(), GETDATE(), '2024-04-09 12:00:00', '2024-04-09 14:00:00', 2, 2, 1, 'Bug Fixing'),
(66, 1, GETDATE(), GETDATE(), '2024-04-09 14:30:00', '2024-04-09 17:00:00', 3, 3, 1, 'Documentation'),

-- Wednesday, 10th of April
(67, 1, GETDATE(), GETDATE(), '2024-04-10 09:00:00', '2024-04-10 12:00:00', 1, 1, 1, 'Feature Implementation'),
(68, 1, GETDATE(), GETDATE(), '2024-04-10 12:30:00', '2024-04-10 14:00:00', 2, NULL, 1, 'Internal Meeting'),
(69, 1, GETDATE(), GETDATE(), '2024-04-10 15:00:00', '2024-04-10 17:00:00', 3, NULL, 1, 'Code Refactoring'),

-- Thursday, 11th of April
(70, 1, GETDATE(), GETDATE(), '2024-04-11 09:00:00', '2024-04-11 11:00:00', 1, 1, 1, 'Feature Testing'),
(71, 1, GETDATE(), GETDATE(), '2024-04-11 12:00:00', '2024-04-11 14:00:00', 2, 2, 1, 'Client Demo'),
(72, 1, GETDATE(), GETDATE(), '2024-04-11 14:30:00', '2024-04-11 17:00:00', 3, 3, 1, 'Deployment Preparation'),

-- Friday, 12th of April
(73, 1, GETDATE(), GETDATE(), '2024-04-12 09:00:00', '2024-04-12 12:00:00', 1, 1, 1, 'Sprint Planning'),
(74, 1, GETDATE(), GETDATE(), '2024-04-12 12:30:00', '2024-04-12 14:00:00', 2, NULL, 1, 'Code Review'),
(75, 1, GETDATE(), GETDATE(), '2024-04-12 15:00:00', '2024-04-12 17:00:00', 3, NULL, 1, 'Documentation'),

-- Monday, 15th of April
(76, 1, GETDATE(), GETDATE(), '2024-04-15 09:00:00', '2024-04-15 12:00:00', 1, 1, 1, 'Feature Development'),
(77, 1, GETDATE(), GETDATE(), '2024-04-15 13:00:00', '2024-04-15 15:00:00', 2, NULL, 1, 'Client Meeting'),
(78, 1, GETDATE(), GETDATE(), '2024-04-15 15:30:00', '2024-04-15 17:00:00', 3, NULL, 1, 'Code Review'),

-- Tuesday, 16th of April
(79, 1, GETDATE(), GETDATE(), '2024-04-16 09:00:00', '2024-04-16 11:00:00', 1, 1, 1, 'Testing'),
(80, 1, GETDATE(), GETDATE(), '2024-04-16 12:00:00', '2024-04-16 14:00:00', 2, 2, 1, 'Bug Fixing'),
(81, 1, GETDATE(), GETDATE(), '2024-04-16 14:30:00', '2024-04-16 17:00:00', 3, 3, 1, 'Documentation'),

-- Wednesday, 17th of April
(82, 1, GETDATE(), GETDATE(), '2024-04-17 09:00:00', '2024-04-17 12:00:00', 1, 1, 1, 'Feature Implementation'),
(83, 1, GETDATE(), GETDATE(), '2024-04-17 12:30:00', '2024-04-17 14:00:00', 2, NULL, 1, 'Internal Meeting'),
(84, 1, GETDATE(), GETDATE(), '2024-04-17 15:00:00', '2024-04-17 17:00:00', 3, NULL, 1, 'Code Refactoring'),

-- Thursday, 18th of April
(85, 1, GETDATE(), GETDATE(), '2024-04-18 09:00:00', '2024-04-18 11:00:00', 1, 1, 1, 'Feature Testing'),
(86, 1, GETDATE(), GETDATE(), '2024-04-18 12:00:00', '2024-04-18 14:00:00', 2, 2, 1, 'Client Demo'),
(87, 1, GETDATE(), GETDATE(), '2024-04-18 14:30:00', '2024-04-18 17:00:00', 3, 3, 1, 'Deployment Preparation'),

-- Friday, 19th of April
(88, 1, GETDATE(), GETDATE(), '2024-04-19 09:00:00', '2024-04-19 12:00:00', 1, 1, 1, 'Sprint Planning'),
(89, 1, GETDATE(), GETDATE(), '2024-04-19 12:30:00', '2024-04-19 14:00:00', 2, NULL, 1, 'Code Review'),
(90, 1, GETDATE(), GETDATE(), '2024-04-19 15:00:00', '2024-04-19 17:00:00', 3, NULL, 1, 'Documentation'),

-- Monday, 22nd of April
(91, 1, GETDATE(), GETDATE(), '2024-04-22 09:00:00', '2024-04-22 12:00:00', 1, 1, 1, 'Feature Development'),
(92, 1, GETDATE(), GETDATE(), '2024-04-22 13:00:00', '2024-04-22 15:00:00', 2, NULL, 1, 'Client Meeting'),
(93, 1, GETDATE(), GETDATE(), '2024-04-22 15:30:00', '2024-04-22 17:00:00', 3, NULL, 1, 'Code Review'),

-- Tuesday, 23rd of April
(94, 1, GETDATE(), GETDATE(), '2024-04-23 09:00:00', '2024-04-23 11:00:00', 1, 1, 1, 'Testing'),
(95, 1, GETDATE(), GETDATE(), '2024-04-23 12:00:00', '2024-04-23 14:00:00', 2, 2, 1, 'Bug Fixing'),
(96, 1, GETDATE(), GETDATE(), '2024-04-23 14:30:00', '2024-04-23 17:00:00', 3, 3, 1, 'Documentation'),

-- Wednesday, 24th of April
(97, 1, GETDATE(), GETDATE(), '2024-04-24 09:00:00', '2024-04-24 12:00:00', 1, 1, 1, 'Feature Implementation'),
(98, 1, GETDATE(), GETDATE(), '2024-04-24 12:30:00', '2024-04-24 14:00:00', 2, NULL, 1, 'Internal Meeting'),
(99, 1, GETDATE(), GETDATE(), '2024-04-24 15:00:00', '2024-04-24 17:00:00', 3, NULL, 1, 'Code Refactoring'),

-- Thursday, 25th of April
(100, 1, GETDATE(), GETDATE(), '2024-04-25 09:00:00', '2024-04-25 11:00:00', 1, 1, 1, 'Feature Testing'),
(101, 1, GETDATE(), GETDATE(), '2024-04-25 12:00:00', '2024-04-25 14:00:00', 2, 2, 1, 'Client Demo'),
(102, 1, GETDATE(), GETDATE(), '2024-04-25 14:30:00', '2024-04-25 17:00:00', 3, 3, 1, 'Deployment Preparation'),

-- Friday, 26th of April
(103, 1, GETDATE(), GETDATE(), '2024-04-26 09:00:00', '2024-04-26 12:00:00', 1, 1, 1, 'Sprint Planning'),
(104, 1, GETDATE(), GETDATE(), '2024-04-26 12:30:00', '2024-04-26 14:00:00', 2, NULL, 1, 'Code Review'),
(105, 1, GETDATE(), GETDATE(), '2024-04-26 15:00:00', '2024-04-26 17:00:00', 3, NULL, 1, 'Documentation'),

-- Monday, 29th of April
(106, 1, GETDATE(), GETDATE(), '2024-04-29 09:00:00', '2024-04-29 12:00:00', 1, 1, 1, 'Feature Development'),
(107, 1, GETDATE(), GETDATE(), '2024-04-29 13:00:00', '2024-04-29 15:00:00', 2, NULL, 1, 'Client Meeting'),
(108, 1, GETDATE(), GETDATE(), '2024-04-29 15:30:00', '2024-04-29 17:00:00', 3, NULL, 1, 'Code Review'),

-- Tuesday, 30th of April
(109, 1, GETDATE(), GETDATE(), '2024-04-30 09:00:00', '2024-04-30 11:00:00', 1, 1, 1, 'Testing'),
(110, 1, GETDATE(), GETDATE(), '2024-04-30 12:00:00', '2024-04-30 14:00:00', 2, 2, 1, 'Bug Fixing'),
(111, 1, GETDATE(), GETDATE(), '2024-04-30 14:30:00', '2024-04-30 17:00:00', 3, 3, 1, 'Documentation'),

-- Wednesday, 1st of May
(112, 1, GETDATE(), GETDATE(), '2024-05-01 09:00:00', '2024-05-01 12:00:00', 1, 1, 1, 'Feature Implementation'),
(113, 1, GETDATE(), GETDATE(), '2024-05-01 12:30:00', '2024-05-01 14:00:00', 2, NULL, 1, 'Internal Meeting'),
(114, 1, GETDATE(), GETDATE(), '2024-05-01 15:00:00', '2024-05-01 17:00:00', 3, NULL, 1, 'Code Refactoring'),

-- Thursday, 2nd of May
(115, 1, GETDATE(), GETDATE(), '2024-05-02 09:00:00', '2024-05-02 11:00:00', 1, 1, 1, 'Feature Testing'),
(116, 1, GETDATE(), GETDATE(), '2024-05-02 12:00:00', '2024-05-02 14:00:00', 2, 2, 1, 'Client Demo'),
(117, 1, GETDATE(), GETDATE(), '2024-05-02 14:30:00', '2024-05-02 17:00:00', 3, 3, 1, 'Deployment Preparation'),

-- Friday, 3rd of May
(118, 1, GETDATE(), GETDATE(), '2024-05-03 09:00:00', '2024-05-03 12:00:00', 1, 1, 1, 'Sprint Planning'),
(119, 1, GETDATE(), GETDATE(), '2024-05-03 12:30:00', '2024-05-03 14:00:00', 2, NULL, 1, 'Code Review'),
(120, 1, GETDATE(), GETDATE(), '2024-05-03 15:00:00', '2024-05-03 17:00:00', 3, NULL, 1, 'Documentation'),

-- Monday, 6th of May
(121, 1, GETDATE(), GETDATE(), '2024-05-06 09:00:00', '2024-05-06 12:00:00', 1, 1, 1, 'Feature Development'),
(122, 1, GETDATE(), GETDATE(), '2024-05-06 13:00:00', '2024-05-06 15:00:00', 2, NULL, 1, 'Client Meeting'),
(123, 1, GETDATE(), GETDATE(), '2024-05-06 15:30:00', '2024-05-06 17:00:00', 3, NULL, 1, 'Code Review'),

-- Tuesday, 7th of May
(124, 1, GETDATE(), GETDATE(), '2024-05-07 09:00:00', '2024-05-07 11:00:00', 1, 1, 1, 'Testing'),
(125, 1, GETDATE(), GETDATE(), '2024-05-07 12:00:00', '2024-05-07 14:00:00', 2, 2, 1, 'Bug Fixing'),
(126, 1, GETDATE(), GETDATE(), '2024-05-07 14:30:00', '2024-05-07 17:00:00', 3, 3, 1, 'Documentation'),

-- Wednesday, 8th of May
(127, 1, GETDATE(), GETDATE(), '2024-05-08 09:00:00', '2024-05-08 12:00:00', 1, 1, 1, 'Feature Implementation'),
(128, 1, GETDATE(), GETDATE(), '2024-05-08 12:30:00', '2024-05-08 14:00:00', 2, NULL, 1, 'Internal Meeting'),
(129, 1, GETDATE(), GETDATE(), '2024-05-08 15:00:00', '2024-05-08 17:00:00', 3, NULL, 1, 'Code Refactoring'),

-- Thursday, 9th of May
(130, 1, GETDATE(), GETDATE(), '2024-05-09 09:00:00', '2024-05-09 11:00:00', 1, 1, 1, 'Feature Testing'),
(131, 1, GETDATE(), GETDATE(), '2024-05-09 12:00:00', '2024-05-09 14:00:00', 2, 2, 1, 'Client Demo'),
(132, 1, GETDATE(), GETDATE(), '2024-05-09 14:30:00', '2024-05-09 17:00:00', 3, 3, 1, 'Deployment Preparation'),

-- Friday, 10th of May
(133, 1, GETDATE(), GETDATE(), '2024-05-10 09:00:00', '2024-05-10 12:00:00', 1, 1, 1, 'Sprint Planning'),
(134, 1, GETDATE(), GETDATE(), '2024-05-10 12:30:00', '2024-05-10 14:00:00', 2, NULL, 1, 'Code Review'),
(135, 1, GETDATE(), GETDATE(), '2024-05-10 15:00:00', '2024-05-10 17:00:00', 3, NULL, 1, 'Documentation'),

-- Monday, 13th of May
(136, 1, GETDATE(), GETDATE(), '2024-05-13 09:00:00', '2024-05-13 12:00:00', 1, 1, 1, 'Feature Development'),
(137, 1, GETDATE(), GETDATE(), '2024-05-13 13:00:00', '2024-05-13 15:00:00', 2, NULL, 1, 'Client Meeting'),
(138, 1, GETDATE(), GETDATE(), '2024-05-13 15:30:00', '2024-05-13 17:00:00', 3, NULL, 1, 'Code Review'),

-- Tuesday, 14th of May
(139, 1, GETDATE(), GETDATE(), '2024-05-14 09:00:00', '2024-05-14 11:00:00', 1, 1, 1, 'Testing'),
(140, 1, GETDATE(), GETDATE(), '2024-05-14 12:00:00', '2024-05-14 14:00:00', 2, 2, 1, 'Bug Fixing'),
(141, 1, GETDATE(), GETDATE(), '2024-05-14 14:30:00', '2024-05-14 17:00:00', 3, 3, 1, 'Documentation'),

-- Wednesday, 15th of May
(142, 1, GETDATE(), GETDATE(), '2024-05-15 09:00:00', '2024-05-15 12:00:00', 1, 1, 1, 'Feature Implementation'),
(143, 1, GETDATE(), GETDATE(), '2024-05-15 12:30:00', '2024-05-15 14:00:00', 2, NULL, 1, 'Internal Meeting'),
(144, 1, GETDATE(), GETDATE(), '2024-05-15 15:00:00', '2024-05-15 17:00:00', 3, NULL, 1, 'Code Refactoring'),

-- Thursday, 16th of May
(145, 1, GETDATE(), GETDATE(), '2024-05-16 09:00:00', '2024-05-16 11:00:00', 1, 1, 1, 'Feature Testing'),
(146, 1, GETDATE(), GETDATE(), '2024-05-16 12:00:00', '2024-05-16 14:00:00', 2, 2, 1, 'Client Demo'),
(147, 1, GETDATE(), GETDATE(), '2024-05-16 14:30:00', '2024-05-16 17:00:00', 3, 3, 1, 'Deployment Preparation'),

-- Friday, 17th of May
(148, 1, GETDATE(), GETDATE(), '2024-05-17 09:00:00', '2024-05-17 12:00:00', 1, 1, 1, 'Sprint Planning'),
(149, 1, GETDATE(), GETDATE(), '2024-05-17 12:30:00', '2024-05-17 14:00:00', 2, NULL, 1, 'Code Review'),
(150, 1, GETDATE(), GETDATE(), '2024-05-17 15:00:00', '2024-05-17 17:00:00', 3, NULL, 1, 'Documentation'),

-- Monday, 20th of May
(151, 1, GETDATE(), GETDATE(), '2024-05-20 09:00:00', '2024-05-20 12:00:00', 1, 1, 1, 'Feature Development'),
(152, 1, GETDATE(), GETDATE(), '2024-05-20 13:00:00', '2024-05-20 15:00:00', 2, NULL, 1, 'Client Meeting'),
(153, 1, GETDATE(), GETDATE(), '2024-05-20 15:30:00', '2024-05-20 17:00:00', 3, NULL, 1, 'Code Review'),

-- Registration belong to different user for testing
(154, 1, GETDATE(), GETDATE(), '2024-05-20 15:30:00', '2024-05-20 17:00:00',1, NULL, 2, 'Code Review');


-- Inserting into 'time_registration_tag' table
INSERT INTO time_registration_tag (time_registration_id, tag_id)
VALUES (1, 1),
       (2, 2),
       (2, 3),
       (3, 3),
       (4, 1),
       (4, 2),
       (4, 4),
       (5, 1),
       (5, 2),
       (6, 2),
       (7, 3),
       (8, 4),
       (9, 1),
       (10, 2),
       (11, 3),
       (12, 4),
       (13, 1),
       (14, 2),
       (15, 3),
       (16, 1),
       (17, 2),
       (17, 3),
       (18, 3),
       (19, 1),
       (20, 2),
       (21, 3),
       (22, 4),
       (23, 1),
       (23, 2),
       (24, 2),
       (24, 3),
       (25, 1),
       (25, 4),
       (26, 3),
       (27, 2),
       (28, 1),
       (38, 1),
       (39, 3),
       (40, 4),
       (41, 1),
       (45, 4),
       (46, 3),
       (47, 2),
       (47, 4),
       (48, 1),
       (49, 3),
       (50, 4),
       (50, 1),
       (51, 3),
       (52, 2),
       (53, 4),
       (54, 1),
       (55, 3),
       (56, 2),
       (57, 4),
       (58, 1),
       (59, 3),
       (60, 4),
       (61, 1),
       (62, 3),
       (63, 2),
       (64, 4),
       (65, 1),
       (66, 3),
       (66, 4),
       (67, 1),
       (68, 2),
       (69, 3),
       (70, 4),
       (71, 1),
       (71, 2),
       (72, 2),
       (72, 3),
       (73, 1),
       (74, 2),
       (76, 4),
       (77, 1),
       (78, 2),
       (79, 3),
       (81, 2),
       (81, 3),
       (82, 3),
       (83, 1),
       (85, 3),
       (86, 1),
       (87, 2),
       (89, 4),
       (90, 1),
       (90, 2),
       (91, 3),
       (92, 1),
       (93, 2),
       (95, 4),
       (96, 1),
       (98, 3),
       (99, 1),
       (101, 3),
       (102, 1),
       (103, 2),
       (105, 1),
       (106, 2),
       (107, 3),
       (109, 1),
       (110, 2),
       (111, 3),
       (114, 3),
       (115, 4),
       (116, 1),
       (116, 2),
       (117, 2),
       (117, 3),
       (119, 2),
       (120, 3),
       (121, 4),
       (122, 1),
       (123, 2),
       (125, 1),
       (126, 2),
       (127, 3),
       (128, 1),
       (129, 2),
       (130, 3),
       (132, 1),
       (133, 2),
       (134, 3),
       (135, 1),
       (136, 2),
       (136, 3),
       (138, 1),
       (139, 2),
       (140, 3),
       (141, 4),
       (142, 1),
       (142, 2),
       (143, 2),
       (144, 1),
       (145, 2),
       (146, 3),
       (147, 4),
       (148, 1),
       (150, 3);


-- Assigning projects to users
INSERT INTO account_project (is_favorite, created_at, updated_at, user_id, project_id)
VALUES (0, GETDATE(), GETDATE(), 1, 1),
       (0, GETDATE(), GETDATE(), 1, 2),
       (0, GETDATE(), GETDATE(), 1, 3),
       (0, GETDATE(), GETDATE(), 1, 4),
       (1, GETDATE(), GETDATE(), 1, 5),
       (0, GETDATE(), GETDATE(), 1, 23),
       (0, GETDATE(), GETDATE(), 1, 24),
       (0, GETDATE(), GETDATE(), 2, 1); -- Project for other user for testing purposes
