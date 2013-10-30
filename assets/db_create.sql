drop table if exists website;

/*==============================================================*/
/* Table: website                                               */
/*==============================================================*/
create table website
(
   site_id              INTEGER not null primary key,                     --ID
   site_name            NVARCHAR(200) not null default '',
   list_url             NVARCHAR(500) not null default '',
   page                 INTEGER not null default 5,
   is_auto              BOOLEAN not null default 0,
   collect_interval     INTEGER not null default 5,
   last_collect_datetime DATETIME not null default '1900-01-01 00:00:00',
   unread_count         INTEGER not null default 0,
   total_count          INTEGER not null default 0,
   enable               BOOLEAN not null default 1,
   remark               NVARCHAR(1000) not null default '',
   createtime           DATETIME not null default '1900-01-01 00:00:00'
);

drop table if exists downloads;

/*==============================================================*/
/* Table: downloads                                             */
/*==============================================================*/
create table downloads
(
   download_id          INTEGER not null primary key,
   weibsite_id          INTEGER not null default 0,
   title                NVARCHAR(200) not null default '',
   url                  NVARCHAR(500) not null default '',
   save_path            NVARCHAR(500) not null default '',
   successed            BOOLEAN not null default 0,
   cleared              BOOLEAN not null default 0,
   readed               BOOLEAN not null default 0,
   download_time        DATETIME not null default '1900-01-01 00:00:00'
);

insert into website(site_name, list_url) values('Other', '');