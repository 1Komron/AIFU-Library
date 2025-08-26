-- Role enum
CREATE TYPE role AS ENUM ('ADMIN', 'SUPER_ADMIN', 'STUDENT');

-- Status enum
CREATE TYPE status AS ENUM ('APPROVED', 'OVERDUE');

-- NotificationType enum
CREATE TYPE notification_type AS ENUM ('WARNING', 'EXTEND');
