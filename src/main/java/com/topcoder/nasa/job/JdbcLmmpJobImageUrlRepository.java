package com.topcoder.nasa.job;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * Plain old {@link JdbcTemplate} implementation of an {@link LmmpJobImageUrlRepository}.
 *
 */
@Repository
public class JdbcLmmpJobImageUrlRepository implements LmmpJobImageUrlRepository {
    @Autowired
    @Qualifier("lmmpJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    // =========================================================================

    private static final String DELETE_SQL = "delete from JobImageUrl where uuid = ?";
    private static final String INSERT_SQL = "insert into JobImageUrl (uuid, url) values (?, ?)";
    private static final String SELECT_SQL = "select url from JobImageUrl where uuid = ?";

    // =========================================================================

    @Override
    public void setImageUrls(final LmmpJob job, final List<String> urls) {
        jdbcTemplate.update(DELETE_SQL, new Object[] { job.getUuid() });

        jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, job.getUuid());
                ps.setString(2, urls.get(i));
            }

            public int getBatchSize() {
                return urls.size();
            }
        });
    }

    @Override
    public List<String> getImageUrls(LmmpJob job) {
        return jdbcTemplate.query(SELECT_SQL, new Object[] { job.getUuid() },
                new RowMapper<String>() {
                    public String mapRow(ResultSet rs, int arg1) throws SQLException {
                        return rs.getString(1);
                    }
                });
    }
}
