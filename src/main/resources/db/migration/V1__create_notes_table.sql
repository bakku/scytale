CREATE TABLE `notes` (
    `id` IDENTITY PRIMARY KEY,
    `identifier` VARCHAR NOT NULL UNIQUE,
    `content` VARCHAR NOT NULL,
    `access_key` VARCHAR NOT NULL,
    `encrypted_content_salt` VARCHAR NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT NOW(),
    `updated_at` DATETIME NOT NULL DEFAULT NOW()
) ;