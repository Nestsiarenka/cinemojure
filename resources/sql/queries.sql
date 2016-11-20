n-- :name create-user! :<! :n
-- :doc creates a new user record
INSERT INTO users
(first_name, last_name, email, login, user_group_id, pass)
VALUES (:first-name, :second-name, :email, :login, :user-group-id, :pass) RETURNING id

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name,
    email = :email, login = :login, user_group_id = :user_group_id,
    last_login = :last_login, is_active = :is_active, pass = :pass
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name get-users :? :*
-- :doc retrieve all users.
SELECT * FROM users

-- :name get-user-role :? :1
-- :doc get group that an user belong.
SELECT user_groups.id, user_groups.alias FROM user_groups
JOIN users ON user_groups.id = users.user_group_id
WHERE users.id = :id

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id
