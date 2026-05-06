drop table if exists project_member;
drop table if exists time_entry;
drop table if exists task;
drop table if exists project;
drop table if exists employee;

create table employee(
                         id int primary key AUTO_INCREMENT,
                         name varchar(225) NOT NULL,
                         email varchar(225) UNIQUE NOT NULL,
                         password varchar(225) NOT NULL,
                         is_project_manager boolean
);

create table project (
                         id int primary key AUTO_INCREMENT,
                         title varchar(225) NOT NULL,
                         description varchar(1080),
                         time_of_creation date,
                         project_manager_id int NOT NULL,
                         deadline date,
                         active boolean,
                         foreign key (project_manager_id) references employee (id)
);


create table task(
                     id int primary key AUTO_INCREMENT,
                     project_id int NOT NULL,
                     parent_task_id int,
                     title varchar(225) NOT NULL,
                     description varchar(1080),
                     time_estimate decimal(6,2),
                     is_high_priority boolean,
                     foreign key (project_id) references project (id) ON DELETE CASCADE,
                     foreign key (parent_task_id) references task (id) ON DELETE CASCADE
);


create table time_entry(
                           id int primary key AUTO_INCREMENT,
                           employee_id int NOT NULL,
                           task_id int NOT NULL,
                           time_of_creation timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           time_spent decimal(6,2) NOT NULL,
                           foreign key (employee_id) references employee(id),
                           foreign key (task_id) references task(id) ON DELETE CASCADE
);

create table project_member(
                               employee_id int,
                               project_id int,
                               primary key(employee_id, project_id),
                               foreign key (employee_id) references employee (id) ON DELETE CASCADE,
                               foreign key (project_id) references project (id) ON DELETE CASCADE
);

INSERT INTO employee (name, email, password, is_project_manager)
VALUES ('Andreas Jensen', 'grey@email.com', '1234', true),
       ('August Skipper', 'beige@email.com', '5678', false),
       ('Daniella Norgren', 'purple@email.com', 'pw', true),
       ('Mads Svanholm', 'green@email.com', '1234', false);

INSERT INTO project (title, description, time_of_creation, project_manager_id, deadline, active)
VALUES  ('Exam Project', 'Our very first project!', '2026-05-04', 1, '2026-05-26', true),
        ('Sample Project', 'The hard sequel', '2026-05-04', 3, '2026-05-25', true),
        ('Amorphous Blob', 'A faceless, eternally hungry organism with a gaping maw', '2026-05-05', 1, '2026-05-24', false);


INSERT INTO project_member (employee_id, project_id)
VALUES   (1, 1),
                                                              (2, 1),
                                                              (3, 2),
                                                              (4, 2),
                                                              (3, 1),
                                                              (4, 1);

INSERT INTO task (project_id, parent_task_id, title, description, time_estimate, is_high_priority)
VALUES (1, null, 'Brew coffee', null, '0.5', true),
       (1, 1, 'Grind the beans', null, '0.25', true),
       (1, 1, 'Pour boiling water', null, '0.25', true),
       (2, null, 'Motivational speech', null, '1', false);

INSERT INTO time_entry (employee_id, task_id, time_spent)
VALUES (1, 2, '0.25'),
       (4, 4, '2.5'),
       (2, 3, '0.25');




