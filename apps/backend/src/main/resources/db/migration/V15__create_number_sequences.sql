-- ADR-0003: geração de quote_number/order_number sob concorrência real.
-- Sequence do Postgres é a garantia de unicidade -- não confiar em
-- "count(*) + 1" em memória, que colide sob duas criações simultâneas.
CREATE SEQUENCE quote_number_seq START WITH 1;
CREATE SEQUENCE order_number_seq START WITH 1;
