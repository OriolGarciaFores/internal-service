create table files (
                          id integer,
                          title varchar(255) not null,
                          type varchar(10) not null,
                          create_date timestamp not null,
                          last_update timestamp not null,
                          primary key (id)
);

CREATE UNIQUE INDEX uk_title_type
    ON files (title, type);