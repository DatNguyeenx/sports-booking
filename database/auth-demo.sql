USE SportsBooking;
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

BEGIN TRY
    BEGIN TRANSACTION;

    INSERT INTO dbo.users (username, password_hash, full_name, role, enabled)
    SELECT sample.username, sample.password_hash, sample.full_name, sample.role, 1
    FROM (VALUES
        (N'admin_demo', '$2a$10$YqV9T/wHTkwT2e5B63US7uSUR9TLnay7VqRJQ4NrBmBvQvBMZ3ueC', N'Quản trị viên demo', 'ADMIN'),
        (N'staff_demo', '$2a$10$Sl8z4Q1VamfIMwRlcuOWeuBjmVCSAZFaY6CcjBD7OuM2vOvIBCZJW', N'Nhân viên demo', 'STAFF'),
        (N'customer_demo', '$2a$10$2mvNtZOYO8sRRbMv0vEiVuf68rKEinf6E0XfAfP2ypkngd3vCYfCW', N'Khách hàng demo', 'CUSTOMER')
    ) AS sample(username, password_hash, full_name, role)
    WHERE NOT EXISTS (
        SELECT 1
        FROM dbo.users AS existing WITH (UPDLOCK, HOLDLOCK)
        WHERE existing.username = sample.username
    );

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0
        ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO

SELECT username, full_name, role, enabled
FROM dbo.users
WHERE username IN (N'admin_demo', N'staff_demo', N'customer_demo')
ORDER BY username;
GO
