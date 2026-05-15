INSERT INTO role (title, price_per_hour)
VALUES ('junior', 500),
       ('senior', 1000);

INSERT INTO employee (name, email, password, role_id)
VALUES ('Andreas Jensen', 'grey@email.com', '1234', 2),
       ('August Skipper', 'beige@email.com', '5678', 1),
       ('Daniella Norgren', 'purple@email.com', 'pw', 2),
       ('Mads Svanholm', 'green@email.com', '1234', 1),
       ('Mickey Mouse', 'red@email.com', '1234', 1);

INSERT INTO project (title, description, time_of_creation, project_creator_id, deadline, active)
VALUES ('Exam Project', 'Our very first project!', '2026-05-04', 1, '2026-05-26', true),
       ('Sample Project', 'The hard sequel', '2026-05-04', 3, '2026-05-25', true),
       ('Amorphous Blob', 'A faceless, eternally hungry organism with a gaping maw', '2026-05-05', 1, '2026-05-24',
        false);


INSERT INTO project_member (employee_id, project_id)
VALUES (1, 1),
       (2, 1),
       (3, 2),
       (4, 2),
       (3, 1),
       (4, 1);

INSERT INTO task (project_id, parent_task_id, assigned_member_id, title, description, time_estimate, is_high_priority, is_done)
VALUES (1, null, 2, 'Brew coffee', null, '1.5', true, false),
       (1, 1, 3, 'Grind the beans', null, '1.0', true, false),
       (1, 1, 1, 'Pour boiling water', null, '1.0', true, false),
       (2, null, 4, 'Motivational speech', null, '1', false,false);

INSERT INTO time_entry (employee_id, task_id, time_spent)
VALUES (1, 2, '1.0'),
       (4, 4, '2.5'),
       (2, 3, '1.0');