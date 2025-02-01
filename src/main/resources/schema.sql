CREATE TABLE tags (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      name VARCHAR(255) NOT NULL
);

CREATE SEQUENCE post_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE posts (
                       id INT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       text TEXT NOT NULL,
                       image_path VARCHAR(255),
                       likes INT DEFAULT 0
);

CREATE TABLE comments (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          text TEXT NOT NULL,
                          post_id INT NOT NULL,
                          FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

CREATE TABLE post_tag (
                          post_id INT NOT NULL,
                          tag_id INT NOT NULL,
                          PRIMARY KEY (post_id, tag_id),
                          FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                          FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);