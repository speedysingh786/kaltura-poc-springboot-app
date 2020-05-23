package com.nessathon.dao;

import com.nessathon.dto.Artefact;
import com.nessathon.dto.MediaType;
import com.nessathon.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DataAccess {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public boolean validateLogin(String username, String password) {

        String passwordDB = jdbcTemplate.query("select au.PASSWORD_HASH from APPUSER au where au.login_id = ?", new Object[]{username}, new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet rs) throws SQLException,
                    DataAccessException {
                return rs.next() ? rs.getString("PASSWORD_HASH") : null;
            }
        });

        return password.equals(passwordDB);
    }

    public List<Artefact> getAllArtefactsShallow(String searchParam) {

        String sql = "select ID, NAME, URLKEY, TYPE, SIZEINBYTES, atst.TRANSCODE_COMPLETE" +
                " from ARTEFACT a left join ARTEFACT_STATUS atst on a.id = atst.ARTEFACT_ID " +
                " where PARENT_ARTEFACT_ID is null ";

        if (searchParam != null && !searchParam.isEmpty()) {

            sql = sql + " and upper(name) like '%" + searchParam.toUpperCase() + "%'";
        }

        List<Artefact> artefacts = jdbcTemplate.query(sql,
                new RowMapper<Artefact>() {

                    @Override
                    public Artefact mapRow(ResultSet rs, int i) throws SQLException {

                        Artefact artefact = new Artefact(rs.getString("NAME"), rs.getString("URLKEY"));
                        artefact.setId(rs.getInt("ID"));
                        artefact.setMediaType(MediaType.valueOf(rs.getString("TYPE")));
                        artefact.setSizeInBytes(rs.getLong("SIZEINBYTES"));
                        artefact.setTranscodeComplete("Y".equals(rs.getString("TRANSCODE_COMPLETE")));
                        return artefact;
                    }
                }
        );

        return artefacts;
    }

    public Artefact getArtefact(int artefactID) {

        Artefact artefact = jdbcTemplate.query("select ID, NAME, URLKEY, TYPE, SIZEINBYTES, atst.TRANSCODE_COMPLETE " +
                        " from ARTEFACT a left join ARTEFACT_STATUS atst on a.id = atst.ARTEFACT_ID " +
                        " where ID = ?", new Object[]{artefactID},
                new ResultSetExtractor<Artefact>() {

                    @Override
                    public Artefact extractData(ResultSet rs) throws SQLException {

                        if (rs.next()) {
                            Artefact artefact = new Artefact(rs.getString("NAME"), rs.getString("URLKEY"));
                            artefact.setId(rs.getInt("ID"));
                            artefact.setMediaType(MediaType.valueOf(rs.getString("TYPE")));
                            artefact.setSizeInBytes(rs.getLong("SIZEINBYTES"));
                            artefact.setTranscodeComplete("Y".equals(rs.getString("TRANSCODE_COMPLETE")));
                            return artefact;
                        }
                        return null;
                    }
                }
        );

        if (artefact != null) {

            List<Artefact> transcodedArtefact = jdbcTemplate.query("select ID, NAME, URLKEY, TYPE, SIZEINBYTES from ARTEFACT where PARENT_ARTEFACT_ID = ?", new Object[]{artefactID},
                    new RowMapper<Artefact>() {

                        @Override
                        public Artefact mapRow(ResultSet rs, int i) throws SQLException {

                            Artefact artefact = new Artefact(rs.getString("NAME"), rs.getString("URLKEY"));
                            artefact.setId(rs.getInt("ID"));
                            artefact.setMediaType(MediaType.valueOf(rs.getString("TYPE")));
                            artefact.setSizeInBytes(rs.getLong("SIZEINBYTES"));
                            artefact.setTranscodeComplete(true);
                            return artefact;
                        }
                    }
            );

            artefact.setTranscodedArtefacts(transcodedArtefact);
        }
        return artefact;
    }

    public void insertArtefact(Artefact artefact) {

        String artefactSql = "insert into ARTEFACT (ID, NAME, URLKEY, TYPE, SIZEINBYTES, PARENT_ARTEFACT_ID) values (?, ?, ?, ?, ?, ?)";
        String artefactStatusSql = "insert into ARTEFACT_STATUS(ARTEFACT_ID, TRANSCODE_COMPLETE) values (?, 'N')";

        jdbcTemplate.update(artefactSql, artefact.getId(), artefact.getName(), artefact.getUrlKey(), artefact.getMediaType().toString(), artefact.getSizeInBytes(), artefact.getParentArtefactID() == -1 ? null : artefact.getParentArtefactID());
        jdbcTemplate.update(artefactStatusSql, artefact.getId());
    }

    public void updateArtefactURL(int artefactID, String url) {

        String sql = "update ARTEFACT set URLKEY = ? where ID = ?";

        jdbcTemplate.update(sql, url, artefactID);
    }

    public void updateArtefactStatus(int artefactID) {

        String sql = "update ARTEFACT_STATUS set TRANSCODE_COMPLETE = 'Y' where ARTEFACT_ID = ?";

        jdbcTemplate.update(sql, artefactID);
    }

    public String getArtefactStatus(int artefactID) {

        String sql = "select TRANSCODE_COMPLETE from ARTEFACT_STATUS where ARTEFACT_ID = ?";

        return jdbcTemplate.queryForObject(sql, String.class);
    }

    public int getNextArtefactSequenceID() {

        String sql = "VALUES (NEXT VALUE FOR SEQ_ARTEFACT_ID)";

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

}
