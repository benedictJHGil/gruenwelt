package event.controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class EventDAO {

	private Connection con = null;
	private PreparedStatement pstmt = null;
	private DataSource ds = null;
	private ResultSet rs = null;

	public void getCon() {
		try {
			Context initctx = new InitialContext();
			Context envctx = (Context) initctx.lookup("java:comp/env");
			ds = (DataSource) envctx.lookup("jdbc/pool");
			con = ds.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<EventVO> listEventsForMain() {

		List<EventVO> eventsList = new ArrayList();

		getCon();

		try {
			String query = "SELECT e.no, e.title, m.name, e.publishedDate, e.isOpened, e.password, e.numOfMaxMembers, e.numOfJoiningMembers, e.numOfComment, e.numOfViews, e.numOfLikes from events as e join Members as m where e.writer = m.no order by publishedDate DESC";
			//System.out.println(query);
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int no = rs.getInt("e.no");
				String title = rs.getString("e.title");
				String writer = rs.getString("m.name");
				String publishedDate = rs.getString("e.publishedDate");
				int isOpened = rs.getInt("e.isOpened");
				int isLocked = rs.getInt("e.password") > 0 ? 1 : 0;
				int numOfMaxMembers = rs.getInt("e.numOfMaxMembers");
				int numOfJoiningMembers = rs.getInt("e.numOfJoiningMembers");
				int numOfComment = rs.getInt("e.numOfComment"); 
				int numOfViews = rs.getInt("e.numOfViews");
				int numOfLikes = rs.getInt("e.numOfLikes");
				EventVO eventVO = new EventVO(no, title, writer, publishedDate, isOpened, isLocked, numOfMaxMembers, numOfJoiningMembers, numOfComment, numOfViews, numOfLikes);
				eventsList.add(eventVO);
			}
			rs.close();
			pstmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return eventsList;
	}
	
	public EventVO getDetailedEvent(int no) {
		
		EventVO detailedEvent = null;

		getCon();

		try {
			//String query = "SELECT * from events WHERE no=?";
			String query = "SELECT e.no, e.title, m.name, e.publishedDate, e.isOpened, e.password, e.numOfMaxMembers, e.numOfJoiningMembers, e.numOfComment, e.numOfViews, e.numOfLikes, e.numOfAttachLinks, e.contents, e.startTime, e.endTime, e.eventPlace from events as e join Members as m  WHERE e.no="+no;
			System.out.println("no from DAO" + no);
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				detailedEvent = new EventVO();
				detailedEvent.setNo(rs.getInt("e.no"));
				detailedEvent.setTitle(rs.getString("e.title"));
				detailedEvent.setWriter(rs.getString("m.name"));
				detailedEvent.setPublishedDate(rs.getString("e.publishedDate"));
				detailedEvent.setIsOpened(rs.getInt("e.isOpened"));
				//detailedEvent.setIsLocked(rs.getInt("isLocked"));
				detailedEvent.setPassword(rs.getInt("e.password"));
				//detailedEvent.setIsLocked(rs.getInt("password") > 0 ? 1 : 0);
				detailedEvent.setNumOfMaxMembers(rs.getInt("e.numOfMaxMembers"));
				detailedEvent.setNumOfJoiningMembers(rs.getInt("e.numOfJoiningMembers"));
				detailedEvent.setNumOfComment(rs.getInt("e.numOfComment"));
				detailedEvent.setNumOfViews(rs.getInt("e.numOfViews"));
				detailedEvent.setNumOfLikes(rs.getInt("e.numOfLikes"));
				detailedEvent.setNumOfAttachLinks(rs.getInt("e.numOfAttachLinks"));
				detailedEvent.setContents(rs.getString("e.contents"));
				detailedEvent.setStartTime(rs.getString("e.startTime"));
				detailedEvent.setEndTime(rs.getString("e.endTime"));
				detailedEvent.setEventPlace(rs.getString("e.eventPlace"));
				
			}
			rs.close();
			pstmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return detailedEvent;
	}
	
	public int insertEvent(EventVO eventVO) {
		
		int insertCount = 0; // executeUpdate() ???????????? ?????? ????????? ?????? ?????? ????????? ????????? ??????

		getCon();

		try {
			// INSERT ????????? ???????????? ????????? ????????? ??? ?????? ???????????? board ???????????? ??????
			// ?????????(board_num) : ?????? ?????? ????????? ???????????? ???????????? null ??? ??????(???????????? ?????? ??????)
			// ?????????(board_name), ????????????(board_pass), ??????(board_subject), ??????(board_content), ?????????(board_file)
			// ????????? ??????(board_re_ref) : ??????????????? ?????? ??? ?????? ????????? ?????????????????? ???????????? -1 ??????
			// ???????????? ??????(board_re_lev) : ?????? ?????? ?????? ???????????? 0 ??????
			// ?????? ?????? ??????(board_re_seq) : ?????? ?????? ?????? ???????????? 0 ??????
			// ?????????(board_readcount) : ??? ???????????? 0 ??????
			// ?????????(board_date) : ?????? DB ??? ?????? ??? ?????? ?????? ??????(now() ????????? ???????????? ???????????? ?????? ??????)

			String query = "INSERT INTO events (title, writer, contents, startTime, endTime, eventPlace, isOpened, password, numOfMaxMembers) VALUES (?,?,?,?,?,?,?,?,?);";
			
			// Connection ??????????????? PreparedStatement ?????? ???????????? ?????? ??????
			pstmt = con.prepareStatement(query);
			// ? ??????????????? ?????????
			pstmt.setString(1, eventVO.getTitle());
			pstmt.setInt(2, eventVO.getWriterNo());
			pstmt.setString(3, eventVO.getContents());
			pstmt.setString(4, eventVO.getStartTime());
			pstmt.setString(5, eventVO.getEndTime());
			pstmt.setString(6, eventVO.getEventPlace());
			pstmt.setInt(7, eventVO.getIsOpened());
			//pstmt.setInt(9, eventVO.getIsLocked());
			pstmt.setInt(8, eventVO.getPassword());
			pstmt.setInt(9, eventVO.getNumOfMaxMembers());
//			pstmt.setInt(6, -1);// board_re_ref(??? ?????? ????????? ????????? ?????? ?????? => ??????????????? -1 ??????)
//			pstmt.setInt(7, 0); // board_re_lev
//			pstmt.setInt(8, 0); // board_re_seq
			//pstmt.setInt(12, 0); // board_readcount
			// ?????? ??????
			insertCount = pstmt.executeUpdate();
			
			pstmt.close();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return insertCount;
	}

	// ========== ??? ?????? ===========
	// => ????????? ?????? ???????????? ????????? ??? ????????? isArticleWriter() ????????? ?????? ??????
	public int deleteEvent(int no) {
		// ??? ??????(board_num) ??? ???????????? ????????? ??????
		int deleteCount = 0;

		getCon();
		
		try {
			String sql = "DELETE FROM events WHERE no=?";
			System.out.println("no from DAO" + no);

			pstmt = con.prepareStatement(sql);
			//pstmt.setInt(1, no);
			deleteCount = pstmt.executeUpdate();

			pstmt.close();
			con.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return deleteCount;
	}
	
}