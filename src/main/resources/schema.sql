CREATE TABLE public.cliente (
    id SERIAL primary key ,
    data_nascimento date,
    data_atualizacao timestamp(6) without time zone,
    data_criacao timestamp(6) without time zone,
    bairro character varying(255),
    cep character varying(255),
    cidade character varying(255),
    complemento character varying(255),
    cpf character varying(255),
    logradouro character varying(255),
    nome character varying(255),
    numero character varying(255),
    tipo character varying(255),
    uf character varying(255)
);

CREATE TABLE public.payment_method (
    id SERIAL primary key ,
    name character varying(255)
);

CREATE TABLE public.reportai (
    id SERIAL primary key ,
    created_at timestamp(6) with time zone,
    referent_month timestamp(6) with time zone,
    report oid,
    user_id character varying(255)
);

CREATE TABLE public.transaction (
    id SERIAL primary key,
    amount numeric(38,2),
    category_id bigint,
    created_at timestamp(6) with time zone,
    date_transaction timestamp(6) with time zone,
    payment_method_id bigint,
    updated_at timestamp(6) with time zone,
    name character varying(255),
    type character varying(255),
    user_id character varying(255),
    CONSTRAINT transaction_type_check CHECK (((type)::text = ANY ((ARRAY['DEPOSIT'::character varying, 'EXPENSE'::character varying, 'INVESTMENT'::character varying])::text[])))
);

CREATE TABLE public.transaction_category (
    id SERIAL primary key,
    name character varying(255)
);

ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT fk3iabppaif3d2a8jjotnly31j2 FOREIGN KEY (payment_method_id) REFERENCES public.payment_method(id);

ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT fkormeo9tlr0vpkeh555nd0umgm FOREIGN KEY (category_id) REFERENCES public.transaction_category(id);
