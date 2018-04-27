package com.database.db.DAO;

import com.database.db.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserDAO {
    private final JdbcTemplate jdbc;

    @Autowired
    public UserDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static UserMapper userMapper = new UserMapper();

    public void insertUser(User u) {
        jdbc.update("INSERT INTO users (about, email, fullname, nickname) VALUES (?, ?, ?, ?)",
                    u.getAbout(), u.getEmail(), u.getFullname(), u.getNickname());

    }

    public User getUserByNickname(String nickname) {
        try {
            return jdbc.queryForObject("SELECT * FROM users WHERE nickname=?::citext", userMapper, nickname);
        } catch (Exception e) {
            return null;
        }
    }

    public User getUserByEmail(String email) {
        try {
            return jdbc.queryForObject("SELECT * FROM users WHERE email=?::citext", userMapper, email);
        } catch (Exception e) {
            return null;
        }
    }

    public List<User> getUsers(User u) {
        try {
            List<Object> parameters = new ArrayList<>();
            parameters.add(u.getNickname());
            parameters.add(u.getEmail());
            return jdbc.query("SELECT * FROM users WHERE nickname=?::citext OR email=?::citext", parameters.toArray(), userMapper);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateUser(User u) {
        jdbc.update("UPDATE users SET about=COALESCE(?, about), email=COALESCE(?, email), fullname=COALESCE(?, fullname) WHERE nickname=?::citext",
                    u.getAbout(), u.getEmail(), u.getFullname(), u.getNickname());

    }


    private static class UserMapper implements RowMapper<User> {
        public User mapRow(ResultSet result, int rowNum) throws SQLException {
            return new User(
                    result.getString("about"),
                    result.getString("email"),
                    result.getString("fullname"),
                    result.getString("nickname")
            );
        }
    }
}