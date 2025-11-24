CREATE TABLE financial_goals (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    target_amount NUMERIC(19, 2) NOT NULL,
    current_amount NUMERIC(19, 2) NOT NULL,
    monthly_contribution NUMERIC(19, 2) NOT NULL,
    time_horizon_months INTEGER NOT NULL,
    expected_return_rate NUMERIC(5, 2) NOT NULL,
    inflation_rate NUMERIC(5, 2) NOT NULL,
    target_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_financial_goals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_financial_goals_user_id ON financial_goals(user_id);
CREATE INDEX idx_financial_goals_created_at ON financial_goals(created_at);

