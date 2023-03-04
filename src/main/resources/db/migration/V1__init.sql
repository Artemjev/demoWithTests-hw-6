create table public.users
(
    id         serial primary key not null,
    name       character varying,
    email      character varying,
    country    character varying,
    is_deleted boolean
);
create unique index users_id_uindex on users using btree (id);

create table public.addresses
(
    id                 serial primary key not null,
    address_has_active boolean,
    city               character varying(255),
    country            character varying(255),
    street             character varying(255),
    employee_id        integer,
    foreign key (employee_id) references public.users (id)
);

