end;
create extension if not exists dblink;

do $$
  begin
    perform dblink_exec('', 'create database org_blog');
    exception when duplicate_database then raise notice '%, skipping', sqlerrm using errcode = sqlstate;
  end
$$;
