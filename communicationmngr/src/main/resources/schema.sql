
create sequence if not exists contact_seq
increment by 50;

create sequence if not exists email_seq
    increment by 50;

create sequence if not exists telephone_seq
    increment by 50;

create sequence if not exists address_seq
    increment by 50;

create sequence if not exists customer_seq
    increment by 50;

create sequence if not exists professional_seq
    increment by 50;

create sequence if not exists note_seq
    increment by 50;

create sequence if not exists message_seq
increment by 50;

create sequence if not exists action_on_message_seq
increment by 50;

create table if not exists contact
(
    id bigint not null
    primary key,
    ssncode  varchar(255),
    category smallint
    constraint contact_category_check
    check ((category >= 0) AND (category <= 2)),
    name     varchar(255),
    surname  varchar(255)
    );

create table if not exists email
(
    id   bigint not null
    primary key,
    mail varchar(255)
    );

create table if not exists address
(
    id            bigint not null
    primary key,
    city          varchar(255),
    country       varchar(255),
    street_name   varchar(255),
    street_number varchar(255)
    );

create table if not exists telephone
(
    id     bigint not null
    primary key,
    number varchar(255)
    );

create table if not exists contact_email
(
    contact_id bigint not null
    constraint fkjhb6oolv2p95xsci34vuoiq00
    references contact,
    email_id   bigint not null
    constraint fk2wlgsyv59totqq1ghc75yvwmc
    references email,
    primary key (contact_id, email_id)
    );

create table if not exists contact_address
(
    contact_id bigint not null
    constraint fkqqxykpjj1qrgxle7cpp0txicc
    references contact,
    address_id bigint not null
    constraint fka63wvjlxiwgo0098siqj9kjav
    references address,
    primary key (contact_id, address_id)
    );

create table if not exists contact_telephone
(
    contact_id   bigint not null
    constraint fkt9ftx0p2u2601fnmsmqdm849v
    references contact,
    telephone_id bigint not null
    constraint fk921in21ry2gn6rqgpevd66lcj
    references telephone,
    primary key (contact_id, telephone_id)
    );

create table if not exists customer
(
    id         bigint not null
    primary key,
    contact_id bigint
    constraint uk_9wi1wa1mp861xeqlfxeyg5t8m
    unique
    constraint fkdw0fbdq1pdvck4bh72ryf4ac
    references contact
);

create table if not exists professional
(
    id               bigint           not null
    primary key,
    daily_rate       double precision not null,
    employment_state smallint
    constraint professional_employment_state_check
    check ((employment_state >= 0) AND (employment_state <= 2)),
    location         varchar(255),
    skills           varchar(255),
    contact_id       bigint
    constraint uk_dnuv084kwtrqlgoi001vd457g
    unique
    constraint fk6rhmwdhqticr8ws58i09q6n1r
    references contact
    );

create table if not exists note
(
    id              bigint not null
    primary key,
    date            date,
    message         varchar(255),
    customer_id     bigint
    constraint fkedpa244ka6vn0p57e3fiub5n4
    references customer,
    professional_id bigint
    constraint fk7ilgvhk19tsej3pmj5503hu3p
    references professional
    );

create table if not exists job_offer
(
    id              bigint           not null
    primary key,
    description     varchar(255),
    duration        integer          not null,
    profit_margin   double precision not null,
    required_skills varchar(255),
    status          smallint
    constraint job_offer_status_check
    check ((status >= 0) AND (status <= 5)),
    customer_id     bigint
    constraint fk6sibwaqbyqo4xihjf3mm7adfd
    references customer,
    professional_id bigint
    constraint fkj0upbpkiplvyl75wp59vy8of7
    references professional
    );

create table if not exists action_on_job
(
    id              bigint not null
    primary key,
    date            timestamp(6),
    note            varchar(255),
    state           smallint
    constraint action_on_job_state_check
    check ((state >= 0) AND (state <= 5)),
    job_id          bigint
    constraint fk3ngitwhjv04p947e4c7c2emw2
    references job_offer,
    professional_id bigint
    constraint fk97j90v19lffd46gkiive5d82o
    references professional
    );

create table if not exists message
(
    id                  bigint not null
    primary key,
    body                varchar(255),
    channel             smallint
    constraint message_channel_check
    check ((channel >= 0) AND (channel <= 4)),
    date                date,
    priority            smallint
    constraint message_priority_check
    check ((priority >= 0) AND (priority <= 2)),
    state               smallint
    constraint message_state_check
    check ((state >= 0) AND (state <= 5)),
    subject             varchar(255),
    address_sender_id   bigint
    constraint fk1jaflbidrwbb9mpd79iudoxil
    references address,
    email_sender_id     bigint
    constraint fk7ice4eswa0tb8852lb7kbsjt3
    references email,
    telephone_sender_id bigint
    constraint fkw3g89wua6r1onewsunlo1c8g
    references telephone
    );


create table if not exists action_on_message
(
    id         bigint not null
    primary key,
    comment    varchar(255),
    date       timestamp(6),
    state      smallint
    constraint action_on_message_state_check
    check ((state >= 0) AND (state <= 5)),
    message_id bigint
    constraint fkifcj7ow9nadi12vllamsn41vc
    references message
    );
