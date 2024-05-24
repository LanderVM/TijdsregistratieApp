
    create sequence account_SEQ start with 1000 increment by 50;

    create sequence customer_SEQ start with 1000 increment by 50;

    create sequence project_SEQ start with 1000 increment by 50;

    create sequence tag_SEQ start with 1000 increment by 50;

    create sequence task_SEQ start with 1000 increment by 50;

    create sequence time_registration_SEQ start with 1000 increment by 50;

    create table account (
        is_active bit not null,
        created_at datetime2(6) not null,
        id bigint not null,
        updated_at datetime2(6),
        family_name varchar(255),
        first_name varchar(255),
        primary key (id)
    );

    create table account_project (
        is_favorite bit,
        created_at datetime2(6) not null,
        project_id bigint not null,
        updated_at datetime2(6),
        user_id bigint not null,
        primary key (project_id, user_id)
    );

    create table auth0_identity (
        user_id bigint,
        auth0id varchar(255) not null,
        primary key (auth0id)
    );

    create table customer (
        is_active bit not null,
        created_at datetime2(6) not null,
        id bigint not null,
        updated_at datetime2(6),
        company_name varchar(255) not null,
        name varchar(255) not null,
        primary key (id)
    );

    create table project (
        end_date date,
        is_active bit not null,
        start_date date not null,
        created_at datetime2(6) not null,
        customer_id bigint not null,
        id bigint not null,
        total_work_minutes bigint,
        updated_at datetime2(6),
        description varchar(767),
        name varchar(255) not null,
        primary key (id)
    );

    create table tag (
        is_active bit not null,
        created_at datetime2(6) not null,
        id bigint not null,
        updated_at datetime2(6),
        name varchar(40) not null,
        primary key (id)
    );

    create table task (
        is_active bit not null,
        assignedProject_id bigint,
        created_at datetime2(6) not null,
        id bigint not null,
        updated_at datetime2(6),
        name varchar(255) not null,
        primary key (id)
    );

    create table time_registration (
        is_active bit not null,
        assignedProject_id bigint,
        assignedTask_id bigint,
        created_at datetime2(6) not null,
        end_time datetime2(6),
        id bigint not null,
        registrar_id bigint,
        start_time datetime2(6),
        updated_at datetime2(6),
        description varchar(767),
        primary key (id)
    );

    create table time_registration_tag (
        tag_id bigint not null,
        time_registration_id bigint not null,
        primary key (tag_id, time_registration_id)
    );

    create unique nonclustered index UK_g79t6me97yoafgbog9ctq87gg 
       on auth0_identity (user_id) where user_id is not null;

    alter table tag 
       add constraint UK_1wdpsed5kna2y38hnbgrnhi5b unique (name);

    alter table account_project 
       add constraint FK8ldgawojtxpdvk1eus84jkslu 
       foreign key (project_id) 
       references project;

    alter table account_project 
       add constraint FKhope8ugd0dqk68jfsys9i4lb0 
       foreign key (user_id) 
       references account;

    alter table auth0_identity 
       add constraint FKk7eopq4bge8pne1vak9xjerms 
       foreign key (user_id) 
       references account;

    alter table project 
       add constraint FKj948tru2ilgqxfxsppp9mom5j 
       foreign key (customer_id) 
       references customer;

    alter table task 
       add constraint FKq3c45sf8jwqt6dfue66cdqaig 
       foreign key (assignedProject_id) 
       references project;

    alter table time_registration 
       add constraint FK39qx73rfvfe1xeypto8mvcp3t 
       foreign key (assignedProject_id) 
       references project;

    alter table time_registration 
       add constraint FKl2bp1w5xjjsyloagdubh7wwn7 
       foreign key (assignedTask_id) 
       references task;

    alter table time_registration 
       add constraint FKamscv82m7lk13brranticdni4 
       foreign key (registrar_id) 
       references account;

    alter table time_registration_tag 
       add constraint FKmuihe8rii5ltrtjktncxtdw39 
       foreign key (tag_id) 
       references tag;

    alter table time_registration_tag 
       add constraint FKq09sjsgg3xfbme1y113o533fx 
       foreign key (time_registration_id) 
       references time_registration;
