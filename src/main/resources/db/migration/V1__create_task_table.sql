CREATE SEQUENCE IF NOT EXISTS task_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE task (
                      id INT NOT NULL PRIMARY KEY,
                      title VARCHAR(255),
                      is_completed BOOLEAN NOT NULL
);