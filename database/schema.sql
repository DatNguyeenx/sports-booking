IF DB_ID(N'SportsBooking') IS NULL
BEGIN
    CREATE DATABASE SportsBooking;
END;
GO

USE SportsBooking;
GO

CREATE TABLE dbo.sport_types (
                                 id BIGINT IDENTITY(1,1) NOT NULL,
                                 name NVARCHAR(100) NOT NULL,

                                 CONSTRAINT PK_sport_types PRIMARY KEY (id),
                                 CONSTRAINT UQ_sport_types_name UNIQUE (name)
);
GO

CREATE TABLE dbo.courts (
                            id BIGINT IDENTITY(1,1) NOT NULL,
                            sport_type_id BIGINT NOT NULL,
                            code VARCHAR(20) NOT NULL,
                            price_per_slot DECIMAL(18,2) NOT NULL,
                            active BIT NOT NULL
                                CONSTRAINT DF_courts_active DEFAULT (1),

                            CONSTRAINT PK_courts PRIMARY KEY (id),

                            CONSTRAINT UQ_courts_code UNIQUE (code),

                            CONSTRAINT FK_courts_sport_types
                                FOREIGN KEY (sport_type_id)
                                    REFERENCES dbo.sport_types(id),

                            CONSTRAINT CK_courts_price
                                CHECK (price_per_slot >= 0)
);
GO

CREATE TABLE dbo.time_slots (
                                id BIGINT IDENTITY(1,1) NOT NULL,
                                start_time TIME(0) NOT NULL,
                                end_time TIME(0) NOT NULL,

                                CONSTRAINT PK_time_slots PRIMARY KEY (id),

                                CONSTRAINT UQ_time_slots_start UNIQUE (start_time),

                                CONSTRAINT CK_time_slots_duration CHECK (
                                    end_time > start_time
                                        AND DATEDIFF(MINUTE, start_time, end_time) = 30
                                    ),

                                CONSTRAINT CK_time_slots_alignment CHECK (
                                    DATEPART(MINUTE, start_time) IN (0, 30)
                                        AND DATEPART(SECOND, start_time) = 0
                                        AND DATEPART(SECOND, end_time) = 0
                                    )
);
GO

CREATE TABLE dbo.users (
                           id BIGINT IDENTITY(1,1) NOT NULL,
                           username NVARCHAR(50) NOT NULL,
                           password_hash VARCHAR(255) NOT NULL,
                           full_name NVARCHAR(100) NOT NULL,
                           role VARCHAR(20) NOT NULL,
                           enabled BIT NOT NULL
                               CONSTRAINT DF_users_enabled DEFAULT (1),

                           CONSTRAINT PK_users PRIMARY KEY (id),

                           CONSTRAINT UQ_users_username UNIQUE (username),

                           CONSTRAINT CK_users_role CHECK (
                               role IN ('CUSTOMER', 'STAFF', 'ADMIN')
                               )
);
GO

CREATE TABLE dbo.bookings (
                              id BIGINT IDENTITY(1,1) NOT NULL,
                              customer_id BIGINT NOT NULL,
                              created_by_id BIGINT NOT NULL,
                              sport_type_id BIGINT NOT NULL,
                              play_date DATE NOT NULL,
                              total_amount DECIMAL(18,2) NOT NULL,

                              status VARCHAR(20) NOT NULL
                                  CONSTRAINT DF_bookings_status DEFAULT ('CONFIRMED'),

                              payment_status VARCHAR(20) NOT NULL
                                  CONSTRAINT DF_bookings_payment DEFAULT ('UNPAID'),

                              created_at DATETIME2(0) NOT NULL
        CONSTRAINT DF_bookings_created_at DEFAULT (SYSDATETIME()),

                              paid_at DATETIME2(0) NULL,

                              CONSTRAINT PK_bookings PRIMARY KEY (id),

                              CONSTRAINT FK_bookings_customer
                                  FOREIGN KEY (customer_id)
                                      REFERENCES dbo.users(id),

                              CONSTRAINT FK_bookings_created_by
                                  FOREIGN KEY (created_by_id)
                                      REFERENCES dbo.users(id),

                              CONSTRAINT FK_bookings_sport_type
                                  FOREIGN KEY (sport_type_id)
                                      REFERENCES dbo.sport_types(id),

                              CONSTRAINT CK_bookings_total CHECK (
                                  total_amount >= 0
                                  ),

                              CONSTRAINT CK_bookings_status CHECK (
                                  status IN ('CONFIRMED', 'CANCELLED', 'COMPLETED')
                                  ),

                              CONSTRAINT CK_bookings_payment CHECK (
                                  payment_status IN ('UNPAID', 'PAID')
                                  ),

                              CONSTRAINT CK_bookings_paid_at CHECK (
                                  (payment_status = 'UNPAID' AND paid_at IS NULL)
                                      OR
                                  (payment_status = 'PAID' AND paid_at IS NOT NULL)
                                  ),

                              CONSTRAINT CK_bookings_state_consistency CHECK (
                                  (status = 'CONFIRMED')
                                      OR
                                  (status = 'CANCELLED' AND payment_status = 'UNPAID')
                                      OR
                                  (status = 'COMPLETED' AND payment_status = 'PAID')
                                  )
);
GO

CREATE TABLE dbo.court_schedules (
                                     id BIGINT IDENTITY(1,1) NOT NULL,
                                     court_id BIGINT NOT NULL,
                                     time_slot_id BIGINT NOT NULL,
                                     play_date DATE NOT NULL,

                                     status VARCHAR(20) NOT NULL
                                         CONSTRAINT DF_court_schedules_status
                                         DEFAULT ('OPEN'),

                                     active_booking_id BIGINT NULL,

                                     CONSTRAINT PK_court_schedules PRIMARY KEY (id),

                                     CONSTRAINT UQ_court_schedules_court_date_slot
                                         UNIQUE (court_id, play_date, time_slot_id),

                                     CONSTRAINT FK_court_schedules_court
                                         FOREIGN KEY (court_id)
                                             REFERENCES dbo.courts(id),

                                     CONSTRAINT FK_court_schedules_time_slot
                                         FOREIGN KEY (time_slot_id)
                                             REFERENCES dbo.time_slots(id),

                                     CONSTRAINT FK_court_schedules_active_booking
                                         FOREIGN KEY (active_booking_id)
                                             REFERENCES dbo.bookings(id),

                                     CONSTRAINT CK_court_schedules_status CHECK (
                                         status IN ('OPEN', 'MAINTENANCE')
                                         ),

                                     CONSTRAINT CK_court_schedules_maintenance CHECK (
                                         status <> 'MAINTENANCE'
                                             OR active_booking_id IS NULL
                                         )
);
GO

CREATE TABLE dbo.booking_details (
                                     id BIGINT IDENTITY(1,1) NOT NULL,
                                     booking_id BIGINT NOT NULL,
                                     schedule_id BIGINT NOT NULL,
                                     unit_price DECIMAL(18,2) NOT NULL,

                                     CONSTRAINT PK_booking_details PRIMARY KEY (id),

                                     CONSTRAINT UQ_booking_details_booking_schedule
                                         UNIQUE (booking_id, schedule_id),

                                     CONSTRAINT FK_booking_details_booking
                                         FOREIGN KEY (booking_id)
                                             REFERENCES dbo.bookings(id),

                                     CONSTRAINT FK_booking_details_schedule
                                         FOREIGN KEY (schedule_id)
                                             REFERENCES dbo.court_schedules(id),

                                     CONSTRAINT CK_booking_details_price CHECK (
                                         unit_price >= 0
                                         )
);
GO