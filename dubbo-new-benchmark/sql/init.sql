create table cpu_results
(
    id           serial
        constraint cpu_results_pk
            primary key,
    created_time timestamp default now() not null,
    start_time   timestamp default now() not null,
    end_time     timestamp default now() not null,
    cpu_load     real,
    protocol     varchar(256),
    cpu_min      real,
    cpu_max      real,
    side         varchar(128)
);

comment on table cpu_results is 'cpu压测结果';

alter table cpu_results
    owner to admin;

create unique index cpu_results_created_time_uindex
    on cpu_results (created_time);

create table jmh_results
(
    id            serial
        constraint jmh_results_pk
            primary key,
    benchmark     varchar(256)            not null,
    mode          varchar(64)             not null,
    cnt           bigint                  not null,
    score         real                    not null,
    error         real                    not null,
    units         varchar(128)            not null,
    protocol_name varchar(256)            not null,
    created_time  timestamp default now() not null,
    rpc_version   varchar(64)
);

comment on table jmh_results is 'jmh测试结果表';

comment on column jmh_results.protocol_name is '协议名称，比如dubbo, triple';

alter table jmh_results
    owner to admin;

create index jmh_results_created_time_index
    on jmh_results (created_time);

create table mem_results
(
    id           serial
        constraint mem_results_pk
            primary key,
    created_time timestamp default now() not null,
    start_time   timestamp default now() not null,
    end_time     timestamp default now() not null,
    mem_load     real,
    protocol     varchar(256),
    mem_max      real,
    mem_min      real,
    side         varchar(128)
);

comment on table mem_results is 'mem压测结果';

alter table mem_results
    owner to admin;

create index mem_results_created_time_index
    on mem_results (created_time);

