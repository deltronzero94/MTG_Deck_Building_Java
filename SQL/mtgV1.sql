CREATE TABLE IF NOT EXISTS MTGSet
(
    SetName VARCHAR(150) NOT NULL,
    SetCode VARCHAR(20) NOT NULL,
        PRIMARY KEY (SetName, SetCode)
);

CREATE TABLE IF NOT EXISTS MTGSet_Information
(

);