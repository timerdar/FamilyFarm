--
-- PostgreSQL database dump
--

-- Dumped from database version 15.4
-- Dumped by pg_dump version 15.3

-- Started on 2024-01-16 10:36:52

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3408 (class 1262 OID 16384)
-- Name: farm; Type: DATABASE; Schema: -; Owner: admin
--

CREATE DATABASE farm WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.UTF-8';


ALTER DATABASE farm OWNER TO admin;

\connect farm

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO admin;

--
-- TOC entry 3409 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: admin
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- TOC entry 228 (class 1255 OID 41160)
-- Name: clear_delivery(); Type: PROCEDURE; Schema: public; Owner: admin
--

CREATE PROCEDURE public.clear_delivery()
    LANGUAGE plpgsql
    AS $$
begin 
	update "order"
	set status_id = (select id from status where status = 'Готово')
	where status_id = (select id from status where status = 'В доставке');
end;
$$;


ALTER PROCEDURE public.clear_delivery() OWNER TO admin;

--
-- TOC entry 230 (class 1255 OID 41166)
-- Name: last_10_orders(character varying); Type: FUNCTION; Schema: public; Owner: admin
--

CREATE FUNCTION public.last_10_orders(consumer_name character varying) RETURNS TABLE(product_name character varying, amount double precision, start_date date)
    LANGUAGE plpgsql
    AS $$
declare
	c_id int;
	s_id int;
begin
	
	select id into s_id
	from status
	where status = 'Готово';

	select id into c_id
	from consumer c 
	where c."name" = consumer_name;
	
	return query
	select p."name" as product_name, o.amount as amount, start_data as start_date 
	from "order" o join product p on p.id = o.product_id
	where o.consumer_id = c_id and status_id = s_id
	order by start_data desc
	limit 10;
	
end
$$;


ALTER FUNCTION public.last_10_orders(consumer_name character varying) OWNER TO admin;

--
-- TOC entry 229 (class 1255 OID 41163)
-- Name: sum_counter(integer); Type: FUNCTION; Schema: public; Owner: admin
--

CREATE FUNCTION public.sum_counter(need_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
declare 
	total_sum int;
begin
	select sum(o."sum") into total_sum
	from "order" o
	where consumer_id = need_id and o.status_id = 2;

	return total_sum;
end;
$$;


ALTER FUNCTION public.sum_counter(need_id integer) OWNER TO admin;

--
-- TOC entry 227 (class 1255 OID 41142)
-- Name: sum_evaluator(); Type: FUNCTION; Schema: public; Owner: admin
--

CREATE FUNCTION public.sum_evaluator() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
begin 
	if (select pp.eval from product pp where pp.id = new.product_id) then
		new."sum" = (select p.price from product p where p.id = new.product_id)	* new.amount;
	else
		new."sum" = 0;
    end if;
   return new;
end;
$$;


ALTER FUNCTION public.sum_evaluator() OWNER TO admin;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 221 (class 1259 OID 41076)
-- Name: consumer; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.consumer (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    street character varying(50) NOT NULL,
    room character varying(20) NOT NULL,
    district_id integer,
    phone character varying,
    CONSTRAINT consumer_phone_check CHECK (((phone)::text ~ '^[0-9+]+$'::text))
);


ALTER TABLE public.consumer OWNER TO admin;

--
-- TOC entry 220 (class 1259 OID 41075)
-- Name: consumer_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.consumer_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.consumer_id_seq OWNER TO admin;

--
-- TOC entry 3410 (class 0 OID 0)
-- Dependencies: 220
-- Name: consumer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.consumer_id_seq OWNED BY public.consumer.id;


--
-- TOC entry 223 (class 1259 OID 41091)
-- Name: order; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public."order" (
    id integer NOT NULL,
    product_id integer,
    consumer_id integer,
    start_data date,
    status_id integer DEFAULT 1,
    amount double precision,
    sum numeric,
    comment character varying(100) DEFAULT ' '::character varying,
    CONSTRAINT orders_amount_check CHECK ((amount > (0)::double precision))
);


ALTER TABLE public."order" OWNER TO admin;

--
-- TOC entry 217 (class 1259 OID 41013)
-- Name: status; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.status (
    id integer NOT NULL,
    status character varying(50) NOT NULL
);


ALTER TABLE public.status OWNER TO admin;

--
-- TOC entry 226 (class 1259 OID 41156)
-- Name: delivery; Type: VIEW; Schema: public; Owner: admin
--

CREATE VIEW public.delivery AS
 SELECT "order".id,
    "order".product_id,
    "order".consumer_id,
    "order".start_data,
    "order".status_id,
    "order".amount,
    "order".sum,
    "order".comment
   FROM public."order"
  WHERE ("order".status_id = ( SELECT status.id
           FROM public.status
          WHERE ((status.status)::text = 'В доставке'::text)));


ALTER TABLE public.delivery OWNER TO admin;

--
-- TOC entry 215 (class 1259 OID 41004)
-- Name: district; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.district (
    id integer NOT NULL,
    district character varying(50) NOT NULL
);


ALTER TABLE public.district OWNER TO admin;

--
-- TOC entry 214 (class 1259 OID 41003)
-- Name: district_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.district_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.district_id_seq OWNER TO admin;

--
-- TOC entry 3411 (class 0 OID 0)
-- Dependencies: 214
-- Name: district_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.district_id_seq OWNED BY public.district.id;


--
-- TOC entry 225 (class 1259 OID 41152)
-- Name: done_orders; Type: VIEW; Schema: public; Owner: admin
--

CREATE VIEW public.done_orders AS
 SELECT "order".id,
    "order".product_id,
    "order".consumer_id,
    "order".start_data,
    "order".status_id,
    "order".amount,
    "order".sum,
    "order".comment
   FROM public."order"
  WHERE ("order".status_id = ( SELECT status.id
           FROM public.status
          WHERE ((status.status)::text = 'Готово'::text)));


ALTER TABLE public.done_orders OWNER TO admin;

--
-- TOC entry 222 (class 1259 OID 41090)
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.orders_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.orders_id_seq OWNER TO admin;

--
-- TOC entry 3412 (class 0 OID 0)
-- Dependencies: 222
-- Name: orders_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.orders_id_seq OWNED BY public."order".id;


--
-- TOC entry 219 (class 1259 OID 41052)
-- Name: product; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.product (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    price numeric,
    eval boolean,
    CONSTRAINT product_price_check CHECK ((price > (0)::numeric))
);


ALTER TABLE public.product OWNER TO admin;

--
-- TOC entry 218 (class 1259 OID 41051)
-- Name: product_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.product_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.product_id_seq OWNER TO admin;

--
-- TOC entry 3413 (class 0 OID 0)
-- Dependencies: 218
-- Name: product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.product_id_seq OWNED BY public.product.id;


--
-- TOC entry 216 (class 1259 OID 41012)
-- Name: status_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.status_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.status_id_seq OWNER TO admin;

--
-- TOC entry 3414 (class 0 OID 0)
-- Dependencies: 216
-- Name: status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.status_id_seq OWNED BY public.status.id;


--
-- TOC entry 224 (class 1259 OID 41148)
-- Name: undone_orders; Type: VIEW; Schema: public; Owner: admin
--

CREATE VIEW public.undone_orders AS
 SELECT "order".id,
    "order".product_id,
    "order".consumer_id,
    "order".start_data,
    "order".status_id,
    "order".amount,
    "order".sum,
    "order".comment
   FROM public."order"
  WHERE ("order".status_id = ( SELECT status.id
           FROM public.status
          WHERE ((status.status)::text = 'Добавлено'::text)));


ALTER TABLE public.undone_orders OWNER TO admin;

--
-- TOC entry 3217 (class 2604 OID 41079)
-- Name: consumer id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.consumer ALTER COLUMN id SET DEFAULT nextval('public.consumer_id_seq'::regclass);


--
-- TOC entry 3214 (class 2604 OID 41007)
-- Name: district id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.district ALTER COLUMN id SET DEFAULT nextval('public.district_id_seq'::regclass);


--
-- TOC entry 3218 (class 2604 OID 41094)
-- Name: order id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public."order" ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);


--
-- TOC entry 3216 (class 2604 OID 41055)
-- Name: product id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.product ALTER COLUMN id SET DEFAULT nextval('public.product_id_seq'::regclass);


--
-- TOC entry 3215 (class 2604 OID 41016)
-- Name: status id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.status ALTER COLUMN id SET DEFAULT nextval('public.status_id_seq'::regclass);


--
-- TOC entry 3400 (class 0 OID 41076)
-- Dependencies: 221
-- Data for Name: consumer; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.consumer (id, name, street, room, district_id, phone) FROM stdin;
10	АльбинаГаниева	Вологодская	52-14	1	+79191585971
11	Ильнур	Кольцевая	7-3	1	+79191919191
2	Марина.Д.	40лет-Ноября	16-25	1	+79898988990
4	Марьям	Х.Давлетшиной	701кв	2	+79174951791
5	АйгульЯнаби	Янаби	24-15	3	+79991112233
6	Айдар	Комарова	38/74	1	+79191585971
7	Арина	Болотная	3	4	+7999999999
12	Рустем	Центральная	25	5	+79177659500
13	Азат	Кольцевая	7-203	1	+79191919999
14	ОльгаП.	Кольцевая	3/1-9	1	+79273394618
15	Динис	Горького	44-131	1	+79919199999
16	Артем	Комарова	30-74	1	89198989988
18	Петр	Кольцевая	35-6	1	8989899898
\.


--
-- TOC entry 3394 (class 0 OID 41004)
-- Dependencies: 215
-- Data for Name: district; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.district (id, district) FROM stdin;
1	Черниковка
2	Проспект
3	Инорс
4	Максимовка
5	Истриково
\.


--
-- TOC entry 3402 (class 0 OID 41091)
-- Dependencies: 223
-- Data for Name: order; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public."order" (id, product_id, consumer_id, start_data, status_id, amount, sum, comment) FROM stdin;
44	31	2	2024-01-13	3	3	0	 
47	25	2	2024-01-13	3	3	300	 
48	25	2	2024-01-13	2	100	10000	 
49	31	2	2024-01-13	2	3	0	 
\.


--
-- TOC entry 3398 (class 0 OID 41052)
-- Dependencies: 219
-- Data for Name: product; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.product (id, name, price, eval) FROM stdin;
25	пя	100	t
26	кя	10	t
31	бройлер	350	f
\.


--
-- TOC entry 3396 (class 0 OID 41013)
-- Dependencies: 217
-- Data for Name: status; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.status (id, status) FROM stdin;
1	Добавлено
2	В доставке
3	Готово
4	Удалено
\.


--
-- TOC entry 3415 (class 0 OID 0)
-- Dependencies: 220
-- Name: consumer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.consumer_id_seq', 19, true);


--
-- TOC entry 3416 (class 0 OID 0)
-- Dependencies: 214
-- Name: district_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.district_id_seq', 5, true);


--
-- TOC entry 3417 (class 0 OID 0)
-- Dependencies: 222
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.orders_id_seq', 49, true);


--
-- TOC entry 3418 (class 0 OID 0)
-- Dependencies: 218
-- Name: product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.product_id_seq', 31, true);


--
-- TOC entry 3419 (class 0 OID 0)
-- Dependencies: 216
-- Name: status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.status_id_seq', 4, true);


--
-- TOC entry 3237 (class 2606 OID 41084)
-- Name: consumer consumer_name_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.consumer
    ADD CONSTRAINT consumer_name_key UNIQUE (name);


--
-- TOC entry 3239 (class 2606 OID 41082)
-- Name: consumer consumer_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.consumer
    ADD CONSTRAINT consumer_pkey PRIMARY KEY (id);


--
-- TOC entry 3225 (class 2606 OID 41011)
-- Name: district district_district_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.district
    ADD CONSTRAINT district_district_key UNIQUE (district);


--
-- TOC entry 3227 (class 2606 OID 41009)
-- Name: district district_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.district
    ADD CONSTRAINT district_pkey PRIMARY KEY (id);


--
-- TOC entry 3241 (class 2606 OID 41098)
-- Name: order orders_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public."order"
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- TOC entry 3233 (class 2606 OID 41060)
-- Name: product product_name_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_name_key UNIQUE (name);


--
-- TOC entry 3235 (class 2606 OID 41058)
-- Name: product product_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);


--
-- TOC entry 3229 (class 2606 OID 41018)
-- Name: status status_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.status
    ADD CONSTRAINT status_pkey PRIMARY KEY (id);


--
-- TOC entry 3231 (class 2606 OID 41020)
-- Name: status status_status_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.status
    ADD CONSTRAINT status_status_key UNIQUE (status);


--
-- TOC entry 3246 (class 2620 OID 41143)
-- Name: order sum_evaluate; Type: TRIGGER; Schema: public; Owner: admin
--

CREATE TRIGGER sum_evaluate BEFORE UPDATE ON public."order" FOR EACH ROW EXECUTE FUNCTION public.sum_evaluator();


--
-- TOC entry 3247 (class 2620 OID 41147)
-- Name: order sum_evaluate_ins; Type: TRIGGER; Schema: public; Owner: admin
--

CREATE TRIGGER sum_evaluate_ins BEFORE INSERT ON public."order" FOR EACH ROW EXECUTE FUNCTION public.sum_evaluator();


--
-- TOC entry 3242 (class 2606 OID 41085)
-- Name: consumer consumer_district_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.consumer
    ADD CONSTRAINT consumer_district_id_fkey FOREIGN KEY (district_id) REFERENCES public.district(id);


--
-- TOC entry 3243 (class 2606 OID 41104)
-- Name: order orders_consumer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public."order"
    ADD CONSTRAINT orders_consumer_id_fkey FOREIGN KEY (consumer_id) REFERENCES public.consumer(id);


--
-- TOC entry 3244 (class 2606 OID 41099)
-- Name: order orders_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public."order"
    ADD CONSTRAINT orders_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.product(id);


--
-- TOC entry 3245 (class 2606 OID 41109)
-- Name: order orders_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public."order"
    ADD CONSTRAINT orders_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.status(id);


-- Completed on 2024-01-16 10:36:53

--
-- PostgreSQL database dump complete
--

