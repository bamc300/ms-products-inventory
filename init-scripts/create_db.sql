DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'products_user') THEN
    CREATE ROLE products_user LOGIN PASSWORD 'products_password_change_me';
  END IF;
  ALTER ROLE products_user WITH LOGIN PASSWORD 'products_password_change_me';

  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'inventory_user') THEN
    CREATE ROLE inventory_user LOGIN PASSWORD 'inventory_password_change_me';
  END IF;
  ALTER ROLE inventory_user WITH LOGIN PASSWORD 'inventory_password_change_me';
END
$$;

SELECT format('CREATE DATABASE %I OWNER %I', 'products_db', 'products_user')
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'products_db')\gexec

SELECT format('CREATE DATABASE %I OWNER %I', 'inventory_db', 'inventory_user')
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'inventory_db')\gexec

SELECT format('ALTER DATABASE %I OWNER TO %I', 'products_db', 'products_user')
WHERE EXISTS (SELECT 1 FROM pg_database WHERE datname = 'products_db')\gexec

SELECT format('ALTER DATABASE %I OWNER TO %I', 'inventory_db', 'inventory_user')
WHERE EXISTS (SELECT 1 FROM pg_database WHERE datname = 'inventory_db')\gexec
