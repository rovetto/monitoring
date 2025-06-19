create table IF NOT EXISTS public.subscribers
(
    id      uuid default gen_random_uuid() not null primary key,
    email   varchar(255),
    mobile  varchar(255),
    name    varchar(255),
    vorname varchar(255)
    );

alter table public.subscribers
    owner to postgres;

create table IF NOT EXISTS public.probe_subscribers
(
    id            uuid default gen_random_uuid() not null primary key,
    customer      boolean                        not null,
    owner         boolean                        not null,
    probe_id      uuid,
    subscriber_id uuid,
    support       boolean                        not null
    );

alter table public.probe_subscribers
    owner to postgres;


CREATE TABLE IF NOT EXISTS server_groups
(
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    name TEXT                                       NOT NULL
    );

CREATE TABLE IF NOT EXISTS service_groups
(
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    name TEXT                                       NOT NULL
    );

CREATE TABLE IF NOT EXISTS services
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    code             TEXT                                       NOT NULL,
    name             TEXT                                       NOT NULL,
    service_group_id UUID REFERENCES service_groups (id)
    );

CREATE TABLE IF NOT EXISTS servers
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    name            TEXT                                       NOT NULL,
    server_group_id UUID REFERENCES server_groups (id)
    );

INSERT INTO public.server_groups (id, name) VALUES ('a0a2f3d6-06b8-43d3-8dee-db392aaecc2b', 'Linux Servers');

INSERT INTO public.service_groups (id, name) VALUES ('a0a2f3d6-06b8-43d3-8dee-db392aaecc2b', 'Diverse');

INSERT INTO public.servers (id, name, server_group_id) VALUES ('7922329c-1424-413f-b62d-d97a0c19597c', 'ee-securigate', '5e2134f2-cfdd-4137-9f25-d8c911f8721a');

INSERT INTO public.services (id, code, name, service_group_id) VALUES ('7acb31a5-6020-4742-a5f0-15be5cbd0870', '0.001', 'URL', '6a1921ba-5e5d-49a1-95a4-235716d58560');

create table IF NOT EXISTS public.probes
(
    id                uuid default gen_random_uuid() not null primary key,
    check_certificate boolean,
    check_http        boolean,
    check_rule        boolean,
    environment       varchar(255)
    constraint probes_environment_id_check
    check ((environment)::text = ANY
((ARRAY ['local'::character varying, 'dev'::character varying, 'test'::character varying, 'prod'::character varying, 'none'::character varying])::text[])),
    maintenance       boolean,
    name              varchar(255),
    port              integer                        not null,
    url            varchar(255),
    remark            varchar(255),
    rule              varchar(255),
    service_id        uuid,
    server_id         uuid
    );

alter table public.probes
    owner to postgres;

INSERT INTO public.probes (id, check_certificate, check_http, check_rule, environment, maintenance, name, port, url, remark, rule, service_id, server_id) VALUES ('367b3bb6-7b2e-4bd5-a53f-dacef0ed8351', false, false, false, 'prod', false, null, 0, 'https://smar-conferences.org', null, null, '7acb31a5-6020-4742-a5f0-15be5cbd0870', null);
