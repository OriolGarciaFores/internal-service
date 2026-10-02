create table reminder (
                          id integer,
                          title varchar(255) not null,
                          message varchar(255) not null,
                          next_execution timestamp not null,
                          recurrence varchar(20) not null check ((recurrence in ('NONE','DAILY','WEEKLY','MONTHLY'))),
                          owner_discord_id varchar(100) not null,
                          active boolean not null,
                          last_update timestamp not null,
                          primary key (id)
);

CREATE UNIQUE INDEX uk_owner_title
    ON reminder (owner_discord_id, title);