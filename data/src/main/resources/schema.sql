create table if not exists customer
(
    id   serial primary key,
    name varchar(255)
);

insert into customer(id, name) values (1, 'Josh');

insert into customer(id, name) values (2, 'Jacob');