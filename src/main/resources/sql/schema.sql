drop table if exists project_member;
drop table if exists time_entry;
drop table if exists task;
drop table if exists project;
drop table if exists employee;
drop table if exists role;
create table role
(
    id    int primary key AUTO_INCREMENT,
    title varchar(225) UNIQUE NOT NULL,
    price_per_hour DECIMAL(6,2)NOT NULL
);
create table employee
(
    id       int primary key AUTO_INCREMENT,
    name     varchar(225)        NOT NULL,
    email    varchar(225) UNIQUE NOT NULL,
    password varchar(225)        NOT NULL,
    role_id  int                 NOT NULL,
    foreign key (role_id) references role (id)
);

create table project
(
    id                 int primary key AUTO_INCREMENT,
    title              varchar(225) NOT NULL,
    description        varchar(1080),
    time_of_creation   date,
    project_creator_id int          NOT NULL,
    deadline           date,
    active             boolean,
    foreign key (project_creator_id) references employee (id)
);


create table task
(
    id                 int primary key AUTO_INCREMENT,
    project_id         int          NOT NULL,
    parent_task_id     int,
    assigned_member_id int          NOT NULL,
    title              varchar(225) NOT NULL,
    description        varchar(1080),
    time_estimate      decimal(6, 2),
    is_high_priority   boolean,
    is_done            boolean,
    foreign key (project_id) references project (id) ON DELETE CASCADE,
    foreign key (parent_task_id) references task (id) ON DELETE CASCADE,
    foreign key (assigned_member_id) references employee (id) ON DELETE CASCADE
);


create table time_entry
(
    id               int primary key AUTO_INCREMENT,
    employee_id      int           NOT NULL,
    task_id          int           NOT NULL,
    time_of_creation timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    time_spent       decimal(6, 2) NOT NULL,
    foreign key (employee_id) references employee (id),
    foreign key (task_id) references task (id) ON DELETE CASCADE
);

create table project_member
(
    employee_id int,
    project_id  int,
    primary key (employee_id, project_id),
    foreign key (employee_id) references employee (id) ON DELETE CASCADE,
    foreign key (project_id) references project (id) ON DELETE CASCADE
);