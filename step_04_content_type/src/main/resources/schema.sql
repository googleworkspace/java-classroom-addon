CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY, -- user's unique Google ID
    email VARCHAR(255), -- user's email address
);

CREATE TABLE IF NOT EXISTS attachments (
    id VARCHAR (255) PRIMARY KEY, -- unique attachment id
    image_filename VARCHAR(255), -- attachment image filename
    image_caption VARCHAR(255), -- attachment image caption
)