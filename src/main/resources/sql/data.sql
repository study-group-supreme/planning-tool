INSERT INTO employee (name, email, password, is_project_manager) VALUES ('Andreas Jensen', 'grey@email.com', '1234', true),
                                                                        ('August Skipper', 'beige@email.com', '5678', false),
                                                                        ('Daniella Norgren', 'purple@email.com', 'pw', true),
                                                                        ('Mads Svanholm', 'green@email.com', '1234', false);

INSERT INTO project (title, description, time_of_creation, project_manager_id, deadline, active) VALUES  ('Exam Project', 'Our very first project!', '2026-05-04', 1, '2026-05-26', true),
                                                                                                         ('Sample Project', 'The hard sequel', '2026-05-04', 3, '2026-05-25', true),
                                                                                                         ('Amorphous Blob', 'A faceless, eternally hungry organism with a gaping maw', '2026-05-05', 1, '2026-05-24', false);


INSERT INTO project_member (employee_id, project_id) VALUES   (1, 1),
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