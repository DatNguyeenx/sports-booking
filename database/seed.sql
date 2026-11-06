USE SportsBooking;
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

BEGIN TRY
    BEGIN TRANSACTION;

    INSERT INTO dbo.sport_types (name)
    SELECT sample.name
    FROM (VALUES
        (N'Cầu lông'),
        (N'Bóng đá'),
        (N'Bóng rổ')
    ) AS sample(name)
    WHERE NOT EXISTS (
        SELECT 1
        FROM dbo.sport_types AS existing WITH (UPDLOCK, HOLDLOCK)
        WHERE existing.name = sample.name
    );

    INSERT INTO dbo.courts (
        sport_type_id,
        code,
        price_per_slot,
        active
    )
    SELECT
        sport.id,
        sample.code,
        sample.price_per_slot,
        1
    FROM (VALUES
        (N'Cầu lông', 'CL01', CAST(50000 AS DECIMAL(18,2))),
        (N'Cầu lông', 'CL02', CAST(50000 AS DECIMAL(18,2))),
        (N'Cầu lông', 'CL03', CAST(60000 AS DECIMAL(18,2))),
        (N'Bóng đá',  'BD01', CAST(150000 AS DECIMAL(18,2))),
        (N'Bóng đá',  'BD02', CAST(150000 AS DECIMAL(18,2))),
        (N'Bóng đá',  'BD03', CAST(180000 AS DECIMAL(18,2))),
        (N'Bóng rổ',  'BR01', CAST(100000 AS DECIMAL(18,2))),
        (N'Bóng rổ',  'BR02', CAST(100000 AS DECIMAL(18,2))),
        (N'Bóng rổ',  'BR03', CAST(120000 AS DECIMAL(18,2)))
    ) AS sample(sport_name, code, price_per_slot)
    INNER JOIN dbo.sport_types AS sport
        ON sport.name = sample.sport_name
    WHERE NOT EXISTS (
        SELECT 1
        FROM dbo.courts AS existing WITH (UPDLOCK, HOLDLOCK)
        WHERE existing.code = sample.code
    );

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0
        ROLLBACK TRANSACTION;

    THROW;
END CATCH;
GO

SELECT
    court.code AS CourtCode,
    sport.name AS SportName,
    court.price_per_slot AS PricePer30Minutes,
    court.active AS IsActive
FROM dbo.courts AS court
INNER JOIN dbo.sport_types AS sport
    ON sport.id = court.sport_type_id
ORDER BY sport.name, court.code;
GO
