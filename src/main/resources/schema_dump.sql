--
-- PostgreSQL database dump
--

-- Dumped from database version 11.22
-- Dumped by pg_dump version 11.22

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
-- Name: task_priority; Type: TYPE; Schema: public; Owner: -
--
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'task_priority') THEN
CREATE TYPE public.task_priority AS ENUM (
    'URGENT',
    'CRITICAL',
    'NORMAL',
    'MINOR'
);
END IF;
END$$;

--
-- Name: task_status; Type: TYPE; Schema: public; Owner: -
--
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'task_status') THEN
CREATE TYPE public.task_status AS ENUM (
    'TODO',
    'BACKLOG',
    'IN_PROGRESS',
    'OVERDUE',
    'DONE'
);
    END IF;
END$$;


--
-- Name: user_role; Type: TYPE; Schema: public; Owner: -
--
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
CREATE TYPE public.user_role AS ENUM (
    'USER',
    'ADMIN'
);
    END IF;
END$$;

--
-- Name: text_to_task_status(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE OR REPLACE FUNCTION public.text_to_task_status(text) RETURNS public.task_status
    LANGUAGE sql IMMUTABLE
    AS $_$
    SELECT $1::text::task_status;
$_$;


--
-- Name: CAST (text AS public.task_status); Type: CAST; Schema: -; Owner: -
--

--CREATE CAST (text AS public.task_status) WITH FUNCTION public.text_to_task_status(text);


DO $$
BEGIN
    -- Check if cast exists
    IF EXISTS (
        SELECT 1 FROM pg_cast c
        JOIN pg_type s ON c.castsource = s.oid
        JOIN pg_type t ON c.casttarget = t.oid
        WHERE s.typname = 'text' AND t.typname = 'task_status'
    ) THEN
        -- Drop the existing cast if it exists
        EXECUTE 'DROP CAST (text AS public.task_status)';
    END IF;

    -- Recreate the cast
    EXECUTE 'CREATE CAST (text AS public.task_status) WITH FUNCTION public.text_to_task_status(text)';

    RAISE NOTICE 'Cast from text to task_status created/replaced';
EXCEPTION WHEN OTHERS THEN
    RAISE WARNING 'Failed to create text->task_status cast: %', SQLERRM;
END$$;

--
-- Name: pg_get_complete_tabledef(text); Type: FUNCTION; Schema: public; Owner: -
--


--
-- Name: audit_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.audit_log (
    id integer NOT NULL PRIMARY KEY,
    username character varying(255) NOT NULL,
    action character varying(100) NOT NULL,
    description text,
    "timestamp" timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    details jsonb
);


--
-- Name: audit_log_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS public.audit_log_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: audit_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.audit_log_id_seq OWNED BY public.audit_log.id;


--
-- Name: task_comments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.task_comments (
    id bigint NOT NULL PRIMARY KEY,
    task_id bigint NOT NULL,
    user_id bigint NOT NULL,
    msg text NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: task_comments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS public.task_comments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: task_comments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.task_comments_id_seq OWNED BY public.task_comments.id;


--
-- Name: tasks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.tasks (
    id bigint NOT NULL PRIMARY KEY,
    title character varying(100) NOT NULL,
    description text,
    status public.task_status DEFAULT 'TODO'::public.task_status NOT NULL,
    due_date timestamp without time zone,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    created_by bigint NOT NULL,
    assigned_to bigint,
    priority public.task_priority DEFAULT 'NORMAL'::public.task_priority NOT NULL
);


--
-- Name: tasks_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS public.tasks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tasks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.tasks_id_seq OWNED BY public.tasks.id;


--
-- Name: user_credentials; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.user_credentials (
    user_id numeric(38,0) NOT NULL PRIMARY KEY,
    password_hash character varying(255) NOT NULL,
    role character varying(255) DEFAULT 'USER'::public.user_role NOT NULL
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.users (
    id numeric(38,0) NOT NULL PRIMARY KEY,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: audit_log id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_log ALTER COLUMN id SET DEFAULT nextval('public.audit_log_id_seq'::regclass);


--
-- Name: task_comments id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_comments ALTER COLUMN id SET DEFAULT nextval('public.task_comments_id_seq'::regclass);


--
-- Name: tasks id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tasks ALTER COLUMN id SET DEFAULT nextval('public.tasks_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: audit_log audit_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

--ALTER TABLE ONLY public.audit_log
--    ADD CONSTRAINT audit_log_pkey PRIMARY KEY (id);


--
-- Name: task_comments task_comments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

--ALTER TABLE ONLY public.task_comments
--    ADD CONSTRAINT task_comments_pkey PRIMARY KEY (id);


--
-- Name: tasks tasks_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

--ALTER TABLE ONLY public.tasks
--    ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);


--
-- Name: user_credentials user_credentials_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--
--
--ALTER TABLE ONLY public.user_credentials
--    ADD CONSTRAINT user_credentials_pkey PRIMARY KEY (user_id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

--ALTER TABLE ONLY public.users
--    ADD CONSTRAINT users_email_key UNIQUE (email);

DO $$
BEGIN
    -- Check if constraint doesn't exist
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'users_email_key'
        AND conrelid = 'public.users'::regclass
    ) THEN
        -- Add the constraint if it doesn't exist
        EXECUTE 'ALTER TABLE public.users ADD CONSTRAINT users_email_key UNIQUE (email)';
        RAISE NOTICE 'Added unique constraint users_email_key';
    END IF;
EXCEPTION WHEN OTHERS THEN
    RAISE WARNING 'Failed to add users_email_key constraint: %', SQLERRM;
END $$;



--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--
--
--ALTER TABLE ONLY public.users
--    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: idx_task_comments_task_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_task_comments_task_id ON public.task_comments USING btree (task_id);


--
-- Name: idx_task_due_date_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_task_due_date_status ON public.tasks USING btree (due_date) WHERE (status <> 'OVERDUE'::public.task_status);


--
-- Name: task_comments task_comments_task_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--
--
--ALTER TABLE ONLY public.task_comments
--    ADD CONSTRAINT task_comments_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.tasks(id);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'task_comments_task_id_fkey'
        AND conrelid = 'public.task_comments'::regclass
    ) THEN
        EXECUTE 'ALTER TABLE public.task_comments ADD CONSTRAINT task_comments_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.tasks(id)';
    END IF;
END $$;


--
-- Name: task_comments task_comments_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

--ALTER TABLE ONLY public.task_comments
--    ADD CONSTRAINT task_comments_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'task_comments_user_id_fkey'
        AND conrelid = 'public.task_comments'::regclass
    ) THEN
        EXECUTE 'ALTER TABLE public.task_comments ADD CONSTRAINT task_comments_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id)';
    END IF;
END $$;

--
-- Name: tasks tasks_assigned_to_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--
--
--ALTER TABLE ONLY public.tasks
--    ADD CONSTRAINT tasks_assigned_to_fkey FOREIGN KEY (assigned_to) REFERENCES public.users(id);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'tasks_assigned_to_fkey'
        AND conrelid = 'public.tasks'::regclass
    ) THEN
        EXECUTE 'ALTER TABLE public.tasks ADD CONSTRAINT tasks_assigned_to_fkey FOREIGN KEY (assigned_to) REFERENCES public.users(id)';
    END IF;
END $$;

--
-- Name: tasks tasks_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

--ALTER TABLE ONLY public.tasks
--    ADD CONSTRAINT tasks_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'tasks_created_by_fkey'
        AND conrelid = 'public.tasks'::regclass
    ) THEN
        EXECUTE 'ALTER TABLE public.tasks ADD CONSTRAINT tasks_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id)';
    END IF;
END $$;

--
-- Name: user_credentials user_credentials_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

--ALTER TABLE ONLY public.user_credentials
--    ADD CONSTRAINT user_credentials_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'user_credentials_user_id_fkey'
        AND conrelid = 'public.user_credentials'::regclass
    ) THEN
        EXECUTE 'ALTER TABLE public.user_credentials ADD CONSTRAINT user_credentials_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id)';
    END IF;
END $$;


--
-- PostgreSQL database dump complete
--

