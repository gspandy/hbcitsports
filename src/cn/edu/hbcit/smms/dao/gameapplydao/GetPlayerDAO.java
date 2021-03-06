package cn.edu.hbcit.smms.dao.gameapplydao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cn.edu.hbcit.smms.dao.databasedao.DBConn;
import cn.edu.hbcit.smms.dao.logindao.LoginDAO;
import cn.edu.hbcit.smms.pojo.Group2item;
import cn.edu.hbcit.smms.pojo.Item;
import cn.edu.hbcit.smms.pojo.Player;
import cn.edu.hbcit.smms.pojo.Player;
import cn.edu.hbcit.smms.pojo.PlayerNum;
import cn.edu.hbcit.smms.pojo.Sports;


/**
 * 报名类
 *
 * 本类的简要描述：
 * 学生、教工报名界面的获取
 *
 * @author 吕志瑶
 * @version 1.00  2012-6-25 新建类
 */
public class GetPlayerDAO {
	private Statement stmt;
	private PreparedStatement pstmt;
	CallableStatement cs = null;
	private Connection conn;
	ResultSet rs;
	protected final Logger log = Logger.getLogger(GetPlayerDAO.class.getName());
	DBConn db = new DBConn();
	
	/**
	 * 根据用户名获取departid
	 * @author 陈系晶
	 * @param username
	 * @return
	 */
	public int getDepartid(String username){
		int rst = 0;
		String sql = "SELECT departid FROM t_sysadmin where username=?";
		conn = db.getConn();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, username);
			rs = pstmt.executeQuery();
			while( rs.next() ){
				rst = rs.getInt(1);
			}
			rs.close();
			pstmt.close();
			db.freeConnection(conn);
		}catch(Exception e){
			log.error("获取部门id失败！");
			log.error(e.getMessage());
		}
		return rst ;
	}
	
	/**
	 * 根据运动会Id和部门Id获取Sp2dpid
	 * @author 陈系晶
	 * @param flag1
	 * @return
	 */
	public int getSp2dpid(int flag1){
		LoginDAO loginDAO = new LoginDAO();
		int sportsid = loginDAO.selectCurrentSportsId();//获取运动会Id
		int departid = flag1;
		int rst = 0;
		String sql = "SELECT id FROM  t_sports2department where departid=? and sportsid=?";
		conn = db.getConn();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, departid);
			pstmt.setInt(2, sportsid);
			rs = pstmt.executeQuery();
			while( rs.next() ){
				rst = rs.getInt(1);
			}
			rs.close();
			pstmt.close();
			db.freeConnection(conn);
		}catch(Exception e){
			log.error("获取运动会-部门联系表id失败！");
			log.error(e.getMessage());
		}
		return rst ;
	}
	/**
	 * 查询所有未分配的运动员号码
	 * @param sp2dpid
	 * @param numtype
	 * @return
	 */
	public ArrayList getPlayernum(int sp2dpid, boolean numtype){
		log.debug(sp2dpid+" WWWWWWWWWWW "+numtype);
		ArrayList list = new ArrayList();
		String sql = "SELECT t_player.playernum FROM t_player,t_playernum WHERE playername IS NULL AND t_playernum.numtype=? AND t_player.sp2dpid=? AND t_playernum.id=t_player.numid";
		DBConn db = new DBConn();
			try{
				conn = db.getConn();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(2, sp2dpid);
				pstmt.setBoolean(1, numtype);
				rs = pstmt.executeQuery();
				while( rs.next() ){
					PlayerNum num = new PlayerNum();
					num.setPlayerNum(rs.getString(1));
					log.debug(rs.getString(1));
					//System.out.println(rs.getString(1)+rs.getString(2));
					list.add(num);
					
				}
				db.freeConnection(rs,pstmt,conn);
			}catch( Exception e){
				log.error(e.getMessage());
			}
			return list;
		}
	/**
	 * 根据sp2dpid查找运动员号码
	 * @param sp2dpid
	 * @return
	 */	
	public ArrayList getPlayerNum(int sp2dpid,int numtype){
		ArrayList list = new ArrayList();
		String sql = "select beginnum,endnum from t_playernum where sp2dpid = ? and numtype = ? ";
		DBConn db = new DBConn();
			try{
				conn = db.getConn();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, sp2dpid);
				pstmt.setInt(2, numtype);
				rs = pstmt.executeQuery();
				while( rs.next() ){
					PlayerNum num = new PlayerNum();
					num.setBeginnum(rs.getString(1));
					num.setEndnum(rs.getString(2));
					//System.out.println(rs.getString(1)+rs.getString(2));
					list.add(num);
					
				}
				db.freeConnection(rs,pstmt,conn);
			}catch( Exception e){
				log.error(e.getMessage());
			}
			return list;
		}
/**
 * 根据运动会名字和组别的类型获得运动会的比赛项目总数	
 * @param sportsname
 * @param grouptype
 * @return
 */
	public int getItemNumber( int sportsid,int grouptype){
		int flag = 0;
		String sql = "SELECT COUNT(itemname) FROM t_item WHERE id IN " +
				"(SELECT itemid FROM t_group2item WHERE matchtype<>0 AND gp2spid IN " +
				"(SELECT id FROM t_group2sports WHERE groupid IN " +
				"(SELECT id FROM t_group WHERE grouptype = ?) AND sportsid IN " +
				"(SELECT id FROM t_sports WHERE sportsid = ?	)))";
		try{
			conn = db.getConn();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, grouptype);
			pstmt.setInt(2, sportsid);
			rs = pstmt.executeQuery();
			while( rs.next() ){
				flag = rs.getInt(1);
			}
			db.freeConnection(rs,pstmt,conn);
		}catch( Exception e){
			e.getStackTrace();
			System.out.println(e.getMessage());
		}
		return flag;
	}
	/**
	 * 根据运动会名字和组别的类型获得运动会的比赛项目
	 * @param sportsname
	 * @param grouptype
	 * @return
	 */
	public ArrayList getItemName(int sportsid,int grouptype){
		ArrayList list = new ArrayList();
		String sql = "SELECT id,itemname,itemtype FROM t_item WHERE id IN " +
				"(SELECT itemid FROM t_group2item WHERE matchtype<>0 AND gp2spid IN " +
				"(SELECT id FROM t_group2sports WHERE groupid IN " +
				"(SELECT id FROM t_group WHERE grouptype = ?) AND sportsid IN " +
				"(SELECT id FROM t_sports WHERE sportsid = ?	)))";
		try{
			conn = db.getConn();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, grouptype);
			pstmt.setInt(2, sportsid);
			rs = pstmt.executeQuery();
			while( rs.next() ){
				Item itemname = new Item();
				itemname.setItemid(rs.getInt(1));
				itemname.setItemname(rs.getString(2));
				itemname.setItemtype(rs.getString(3));
				//log.debug(itemname.getItemid()+"))))"+itemname.getItemname());
				list.add(itemname);
			}
			db.freeConnection(rs,pstmt,conn);
		}catch( Exception e){
			log.error(e.getMessage());
		}
		
		return list;
	} 
/**
 * 获得数据库内时间最近的运动会名字	
 * @return
 */
	public String getSportsName()
	{
		String sportsname = null;
		String sql="SELECT sportsname FROM t_sports ORDER BY sportsbegin DESC LIMIT 1";
		try{
			conn=db.getConn();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				sportsname = rs.getString(1);
			}
			db.freeConnection(rs,pstmt,conn);
		}catch(Exception e){
			e.getStackTrace();
		    System.out.println(e.getMessage());
		}
		
		return sportsname;
	}	
/**
* 获取本届运动会id
* @return
*/
	public int getSportID(){
		int flag = 0;
		String sql = "SELECT id FROM t_sports where current=1";
		conn = db.getConn();
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while( rs.next() ){
				flag = rs.getInt(1);
			}
			db.freeConnection(rs,pstmt,conn);
		} catch (SQLException e) {
			e.printStackTrace();
			//System.out.println("getSportID"+e.getMessage());
		}
		return flag ;
	}
	
	
/**
* 获取本届运动会所有的分组
* @author 陈系晶
* @param sportsid
* @return groupNameList
*/
	public ArrayList selectAllGroupName(){
		ArrayList groupNameList = new ArrayList();
		String sql = "SELECT id,groupname FROM t_group WHERE id IN" +
				"( SELECT groupid FROM t_group2sports WHERE sportsid = ?)" +
				"AND grouptype = 0";
		conn = db.getConn();
		try{
			GetPlayerDAO gpa = new GetPlayerDAO();
			int sid = gpa.getSportID();
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, sid);
			rs=pstmt.executeQuery();
			while(rs.next()){
				Player group = new Player();
				group.setId(rs.getInt(1));
				group.setGroupname(rs.getString(2));
				groupNameList.add(group);
				
			}
			rs.close();
			pstmt.close();
			db.freeConnection(conn);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return groupNameList;
	}
	
	/**
	 * 根据sql语句添加数据
	 * @param sql  要执行的sql语句
	 */
	public void addPlayerBySql(String sql){
		conn = db.getConn();
        try {
            Connection conn = db.getConn();
            if(conn != null){
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.executeUpdate(); 
            }
            //statement.close();
            db.freeConnection(conn);  
            }catch (SQLException e) {                  	
            e.printStackTrace(); } 
    }
	
	/*
	 * 根据sql语句查询运动员号码是否有冲突
	 * 
	 */
	public int selectPlayerNum(int sp2dpid2,boolean numtype){
		int flag = 0;
		String sql = "SELECT id FROM t_playernum WHERE sp2dpid = ? AND numtype = ?";
		try{
			conn = db.getConn();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sp2dpid2);
			pstmt.setBoolean(2, numtype);
			rs = pstmt.executeQuery();
			while( rs.next() ){
				flag = rs.getInt(1); 
			}
			db.freeConnection(rs,pstmt,conn);
		}catch( Exception e){
			e.getStackTrace();
			System.out.println(e.getMessage());
		}
		return flag;
	}
	/*
	 * 根据号码布的id查询库中是否有插入记录
	 */
	
	public int getSumNumId(int sums){
		int flag = 0;
		String sql = "SELECT COUNT(*) FROM t_player WHERE numid = ?";
		try{
			conn = db.getConn();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sums);
			rs = pstmt.executeQuery();
			while( rs.next() ){
				flag = rs.getInt(1);
			}
			db.freeConnection(rs,pstmt,conn);
		}catch( Exception e){
			e.getStackTrace();
			System.out.println(e.getMessage());
		}
		return flag;
	}
		
	
	/*
	 * 根据号码类型查询运动员报名总数
	 */
	
	public int selNameByNumid(int sums){
		int flag = 0;
		String sql = "SELECT COUNT(playername) FROM t_player WHERE numid = ?";
		try{
			conn = db.getConn();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sums);
			rs = pstmt.executeQuery();
			while( rs.next() ){
				flag = rs.getInt(1);
			}
			db.freeConnection(rs,pstmt,conn);
		}catch( Exception e){
			e.getStackTrace();
			System.out.println(e.getMessage());
		}
		return flag;
	}
	
	
	/*
	 * 通过运动会id获取当前运动会的项目限制数量
	 */
	public int selRule(int sportsid){
		int flag = 0;
		String sql = "SELECT perman FROM t_rule WHERE sportsid = ?";
		try{
			conn = db.getConn();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sportsid);
			rs = pstmt.executeQuery();
			while( rs.next() ){
				flag = rs.getInt(1);
			}
			db.freeConnection(rs,pstmt,conn);
		}catch( Exception e){
			e.getStackTrace();
			System.out.println(e.getMessage());
		}
		return flag;
	}
	
	
	public int selRule2(int sportsid){
		int flag = 0;
		String sql = "SELECT perdepartment FROM t_rule WHERE sportsid = ?";
		try{
			conn = db.getConn();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sportsid);
			rs = pstmt.executeQuery();
			while( rs.next() ){
				flag = rs.getInt(1);
			}
			db.freeConnection(rs,pstmt,conn);
		}catch( Exception e){
			e.getStackTrace();
			System.out.println(e.getMessage());
		}
		return flag;
	}
	
	/**
	 * 根据
	 * @param sp2dpid
	 * @param groupid
	 * @return
	 */
	public ArrayList getItemId( int sp2dpid,int groupid){
		ArrayList registitem = new ArrayList();
		String sql = "SELECT registitem FROM t_player WHERE sp2dpid = ? AND groupid = ?";
		try{
			conn = db.getConn();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sp2dpid);
			pstmt.setInt(2, groupid);
			rs = pstmt.executeQuery();
			while( rs.next() ){
				String itemids = rs.getString(1);
				String[] itemid =itemids.split(";");
				for (int i = 0; i < itemid.length; i++){
					String id = itemid[i];
					String flag = groupid+";"+id;
					registitem.add(flag);
				}
				
			}	
			db.freeConnection(rs,pstmt,conn);
		}catch( Exception e){
			e.getStackTrace();
			System.out.println(e.getMessage());
		}
		return registitem;
	}
/**
 * 获取当前运动会截止报名的日期
 */
	public String getRegistend()
	{
		String registend = null;
		String sql="SELECT registend FROM t_sports WHERE current = 1";
		try{
			conn=db.getConn();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				registend = rs.getString(1);
			}
			db.freeConnection(rs,pstmt,conn);
		}catch(Exception e){
			e.getStackTrace();
		    System.out.println(e.getMessage());
		}
		
		return registend;
	}
	
	/**
	 * 根据运动会id获取每组所能报名的项目
	 * @param sportsid
	 * @return
	 */
		
		public ArrayList getGroupItem(int sportsid){
			ArrayList list = new ArrayList();
			String sql = "SELECT t_item.itemname,t_group.groupname FROM t_group " +
					"JOIN t_group2sports ON t_group2sports.groupid = t_group.id " +
					"JOIN t_group2item ON t_group2item.gp2spid = t_group2sports.id " +
					"JOIN t_item ON t_item.id = t_group2item.itemid " +
					"WHERE t_group2sports.sportsid = ? AND t_group2item.matchtype <> 0 ";//AND grouptype=1";
			try{
				conn = db.getConn();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, sportsid);
				rs = pstmt.executeQuery();
				while( rs.next() ){
					Group2item itemnames = new Group2item();
					itemnames.setItemname(rs.getString(1));
					itemnames.setGroupname(rs.getString(2));
					//log.debug(itemname.getItemid()+"))))"+itemname.getItemname());
					list.add(itemnames);
					
				}
				//System.out.println("aaaa"+list.size());
				db.freeConnection(rs,pstmt,conn);
			}catch( Exception e){
				e.getStackTrace();
				System.out.println(e.getMessage());
			}
			//db.freeConnection(conn);
			return list;
		}
		
		/**
		 * 查询当届运动会的所有参赛组别
		 * @param sportsid
		 * @return
		 */
		public ArrayList getGroup(int sportsid){
			ArrayList list = new ArrayList();
			String sql = "SELECT t_group.groupname FROM t_group2sports,t_group WHERE t_group2sports.sportsid=? AND t_group2sports.groupid=t_group.id ";//AND grouptype=1";
			try{
				conn = db.getConn();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, sportsid);
				rs = pstmt.executeQuery();
				while( rs.next() ){
					Group2item itemnames = new Group2item();
					itemnames.setGroupname(rs.getString(1));
					log.debug("查询结果："+itemnames.getGroupname());
					list.add(itemnames);
					
				}
				db.freeConnection(rs,pstmt,conn);
			}catch( Exception e){
				e.getStackTrace();
			}
			//db.freeConnection(conn);
			return list;
		}
		/**
		 * 根据运动会id获取学生组所能报名的项目
		 * 2012-10-17新建
		 * @param sportsid
		 * @return
		 */
			
		public ArrayList getGroupItemStu(int sportsid){
			ArrayList list = new ArrayList();
			String sql = "SELECT t_item.itemname,t_group.groupname FROM t_group " +
						"JOIN t_group2sports ON t_group2sports.groupid = t_group.id " +
						"JOIN t_group2item ON t_group2item.gp2spid = t_group2sports.id " +
						"JOIN t_item ON t_item.id = t_group2item.itemid " +
						"WHERE t_group2sports.sportsid = ? AND t_group2item.matchtype <> 0 AND grouptype=1";
			try{
				conn = db.getConn();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, sportsid);
				rs = pstmt.executeQuery();
				while( rs.next() ){
				Group2item itemnames = new Group2item();
				itemnames.setItemname(rs.getString(1));
				itemnames.setGroupname(rs.getString(2));
				list.add(itemnames);
				}
				db.freeConnection(rs,pstmt,conn);
			}catch( Exception e){
				e.getStackTrace();
				System.out.println(e.getMessage());
			}
			//db.freeConnection(conn);
			return list;
		}
			
			/**
			 * 查询当届运动会的学生参赛组别
			 * @param sportsid
			 * @return
			 */
			public ArrayList getGroupStu(int sportsid){
				ArrayList list = new ArrayList();
				String sql = "SELECT t_group.groupname FROM t_group2sports,t_group WHERE t_group2sports.sportsid=? AND t_group2sports.groupid=t_group.id AND grouptype=1";
				try{
					conn = db.getConn();
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, sportsid);
					rs = pstmt.executeQuery();
					while( rs.next() ){
						Group2item itemnames = new Group2item();
						itemnames.setGroupname(rs.getString(1));
						log.debug("查询结果："+itemnames.getGroupname());
						list.add(itemnames);
						
					}
					db.freeConnection(rs,pstmt,conn);
				}catch( Exception e){
					e.getStackTrace();
				}
				//db.freeConnection(conn);
				return list;
			}
			/**
			 * 根据运动会id获取教工组所能报名的项目
			 * @param sportsid
			 * @return
			 */
				
			public ArrayList getGroupItemTea(int sportsid){
				ArrayList list = new ArrayList();
				String sql = "SELECT t_item.itemname,t_group.groupname FROM t_group " +
							"JOIN t_group2sports ON t_group2sports.groupid = t_group.id " +
							"JOIN t_group2item ON t_group2item.gp2spid = t_group2sports.id " +
							"JOIN t_item ON t_item.id = t_group2item.itemid " +
							"WHERE t_group2sports.sportsid = ? AND t_group2item.matchtype <> 0 AND grouptype=0";
				try{
					conn = db.getConn();
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, sportsid);
					rs = pstmt.executeQuery();
					while( rs.next() ){
					Group2item itemnames = new Group2item();
					itemnames.setItemname(rs.getString(1));
					itemnames.setGroupname(rs.getString(2));
					list.add(itemnames);
					}
					db.freeConnection(rs,pstmt,conn);
				}catch( Exception e){
					e.getStackTrace();
					System.out.println(e.getMessage());
				}
				//db.freeConnection(conn);
				return list;
			}
				
				/**
				 * 查询当届运动会的教工参赛组别
				 * @param sportsid
				 * @return
				 */
				public ArrayList getGroupTea(int sportsid){
					ArrayList list = new ArrayList();
					String sql = "SELECT t_group.groupname FROM t_group2sports,t_group WHERE t_group2sports.sportsid=? AND t_group2sports.groupid=t_group.id AND grouptype=0";
					try{
						conn = db.getConn();
						pstmt = conn.prepareStatement(sql);
						pstmt.setInt(1, sportsid);
						rs = pstmt.executeQuery();
						while( rs.next() ){
							Group2item itemnames = new Group2item();
							itemnames.setGroupname(rs.getString(1));
							log.debug("查询结果："+itemnames.getGroupname());
							list.add(itemnames);
						}
						db.freeConnection(rs,pstmt,conn);
					}catch( Exception e){
						e.getStackTrace();
					}
					//db.freeConnection(conn);
					return list;
				}
				
				/**
				 * 根据sp2dpid查询学生已报运动员信息
				 * @param sp2dpid
				 * @param group2item
				 * @return
				 */
				public int[][] selectPlayerByspSdpid(int sp2dpid, int[][] group2item){
					conn = db.getConn();
					String sql = "SELECT t_player.playersex,t_player.registitem FROM t_player JOIN t_group ON t_player.groupid = t_group.id WHERE" +
							" t_group.grouptype=1 AND t_player.sp2dpid=? AND registitem IS NOT null";
			        try {
			        	PreparedStatement pStatement = conn.prepareStatement(sql);
			        	pStatement.setInt(1, sp2dpid);
			        	rs = pStatement.executeQuery();
			        	while(rs.next()){
			        		String[] items = rs.getString(2).trim().split(";");
			        		int sex = rs.getInt(1);
			        		for (int i = 0; i < items.length; i++){
			        			int itemId = Integer.parseInt(items[i].trim());
			        			for (int itemNum = 0 ; itemNum < group2item.length; itemNum++){
			        				if (sex == group2item[itemNum][0] && itemId == group2item[itemNum][1]){
			        					group2item[itemNum][2]++;
			        					break;
			        				}
			        			}
			        				
			        		}
			        	}
			        	rs.close();
			        	pStatement.close();
			            db.freeConnection(conn);
			        }catch (SQLException e) {                 
			            log.error("添加hashmap已报运动员失败！");
			    		log.error(e.getMessage());   
			        }
			        return group2item;
				}
				
				/**
				 * 根据sportsid查询学生组性别+项目id
				 * @param sportsid
				 * @return
				 */
				public int[][] selectItemByspSdpid(int sportsid){
					int count = 0;
					int count2 = 0;
					int[][] group2item = null;
					conn = db.getConn();
					String sql = "SELECT t_group.groupsex,t_group2item.itemid FROM t_group2sports JOIN t_group ON " +
							"t_group2sports.groupid = t_group.id  JOIN t_group2item ON " +
							"t_group2item.gp2spid = t_group2sports.id WHERE t_group2sports.sportsid = ? AND t_group.grouptype=1 AND t_group2item.matchtype <> 0";
			        try {
			        	PreparedStatement pStatement = conn.prepareStatement(sql);
			        	pStatement.setInt(1, sportsid);
			        	rs = pStatement.executeQuery();
			        	
			        	rs.last();
			        	count = rs.getRow();
			        	//log.debug("???????????????????????"+count);
			        	group2item = new int[count][3];
			        	rs.beforeFirst();
			        	//log.debug("%%%%%%%%%%%%%%%%%%%%%%%%"+rs.getRow());
			        	while(rs.next()){
			        		group2item[count2][0] = rs.getInt(1);
			        		group2item[count2][1] = rs.getInt(2);
			        		group2item[count2][2] = 0;
			        		count2++;
			        	}
			        	//log.debug("???????????????????????"+count);
			        	rs.close();
			        	pStatement.close();
			            db.freeConnection(conn);
			        }catch (SQLException e) {                 
			            log.error("添加hashmap已报运动员失败！");
			    		log.error(e.getMessage());   
			        }
			        return group2item;
				}
	
				/**
				 * 根据sportsid查询学生教工组组别+项目id
				 * @param sportsid
				 * @return
				 */
				public int[][] selectTItemByspSdpid(int sportsid){
					int count = 0;
					int count2 = 0;
					int[][] group2item = null;
					conn = db.getConn();
					String sql = "SELECT t_group.id,t_group2item.itemid FROM t_group2sports JOIN t_group ON t_group2sports.groupid = t_group.id  JOIN " +
							"t_group2item ON t_group2item.gp2spid = t_group2sports.id WHERE t_group2sports.sportsid = ? AND t_group.grouptype=0 AND t_group2item.matchtype <> 0";
			        try {
			        	PreparedStatement pStatement = conn.prepareStatement(sql);
			        	pStatement.setInt(1, sportsid);
			        	rs = pStatement.executeQuery();
			        	
			        	rs.last();
			        	count = rs.getRow();
			        	//log.debug("???????????????????????"+count);
			        	group2item = new int[count][3];
			        	rs.beforeFirst();
			        	//log.debug("%%%%%%%%%%%%%%%%%%%%%%%%"+rs.getRow());
			        	while(rs.next()){
			        		group2item[count2][0] = rs.getInt(1);
			        		group2item[count2][1] = rs.getInt(2);
			        		group2item[count2][2] = 0;
			        		count2++;
			        	}
			        	//log.debug("???????????????????????"+count);
			        	rs.close();
			        	pStatement.close();
			            db.freeConnection(conn);
			        }catch (SQLException e) {                 
			            log.error("添加hashmap已报运动员失败！");
			    		log.error(e.getMessage());   
			        }
			        return group2item;
				}
	
				/**
				 * 根据sp2dpid查询教工已报运动员信息
				 * @param sp2dpid
				 * @param group2item
				 * @return
				 */
				public int[][] selectTPlayerByspSdpid(int sp2dpid, int[][] group2item){
					conn = db.getConn();
					String sql = "SELECT t_player.groupid,t_player.registitem FROM t_player JOIN t_group ON t_player.groupid = t_group.id WHERE" +
							" t_group.grouptype=0 AND t_player.sp2dpid=? AND registitem IS NOT null";
			        try {
			        	PreparedStatement pStatement = conn.prepareStatement(sql);
			        	pStatement.setInt(1, sp2dpid);
			        	rs = pStatement.executeQuery();
			        	while(rs.next()){
			        		String[] items = rs.getString(2).trim().split(";");
			        		int sex = rs.getInt(1);
			        		for (int i = 0; i < items.length; i++){
			        			int itemId = Integer.parseInt(items[i].trim());
			        			for (int itemNum = 0 ; itemNum < group2item.length; itemNum++){
			        				if (sex == group2item[itemNum][0] && itemId == group2item[itemNum][1]){
			        					group2item[itemNum][2]++;
			        					break;
			        				}
			        			}
			        				
			        		}
			        	}
			        	rs.close();
			        	pStatement.close();
			            db.freeConnection(conn);
			        }catch (SQLException e) {                 
			            log.error("添加hashmap已报运动员失败！");
			    		log.error(e.getMessage());   
			        }
			        return group2item;
				}
				
				
				/**
				 * 根据sportsid查询学生学生组组别+项目id
				 * @param sportsid
				 * @return
				 */
				public int[][] selectSItemByspSdpid(int sportsid){
					int count = 0;
					int count2 = 0;
					int[][] group2item = null;
					conn = db.getConn();
					String sql = "SELECT t_group.id,t_group2item.itemid FROM t_group2sports JOIN t_group ON t_group2sports.groupid = t_group.id  JOIN " +
							"t_group2item ON t_group2item.gp2spid = t_group2sports.id WHERE t_group2sports.sportsid = ? AND t_group.grouptype=1 AND t_group2item.matchtype <> 0";
			        try {
			        	PreparedStatement pStatement = conn.prepareStatement(sql);
			        	pStatement.setInt(1, sportsid);
			        	rs = pStatement.executeQuery();
			        	
			        	rs.last();
			        	count = rs.getRow();
			        	//log.debug("???????????????????????"+count);
			        	group2item = new int[count][3];
			        	rs.beforeFirst();
			        	//log.debug("%%%%%%%%%%%%%%%%%%%%%%%%"+rs.getRow());
			        	while(rs.next()){
			        		group2item[count2][0] = rs.getInt(1);
			        		group2item[count2][1] = rs.getInt(2);
			        		group2item[count2][2] = 0;
			        		count2++;
			        	}
			        	//log.debug("???????????????????????"+count);
			        	rs.close();
			        	pStatement.close();
			            db.freeConnection(conn);
			        }catch (SQLException e) {                 
			            log.error("添加hashmap已报运动员失败！");
			    		log.error(e.getMessage());   
			        }
			        return group2item;
				}
	
				/**
				 * 根据sp2dpid查询学生已报运动员信息(修改页面)
				 * @param sp2dpid
				 * @param group2item
				 * @return
				 */
				public int[][] selectSPlayerByspSdpid(int sp2dpid, int[][] group2item){
					conn = db.getConn();
					String sql = "SELECT t_player.groupid,t_player.registitem FROM t_player JOIN t_group ON t_player.groupid = t_group.id WHERE" +
							" t_group.grouptype=1 AND t_player.sp2dpid=? AND registitem IS NOT null ";
			        try {
			        	PreparedStatement pStatement = conn.prepareStatement(sql);
			        	pStatement.setInt(1, sp2dpid);
			        	rs = pStatement.executeQuery();
			        	while(rs.next()){
			        		String[] items = rs.getString(2).trim().split(";");
			        		int sex = rs.getInt(1);
			        		for (int i = 0; i < items.length; i++){
			        			int itemId = Integer.parseInt(items[i].trim());
			        			for (int itemNum = 0 ; itemNum < group2item.length; itemNum++){
			        				if (sex == group2item[itemNum][0] && itemId == group2item[itemNum][1]){
			        					group2item[itemNum][2]++;
			        					break;
			        				}
			        			}
			        				
			        		}
			        	}
			        	rs.close();
			        	pStatement.close();
			            db.freeConnection(conn);
			        }catch (SQLException e) {                 
			            log.error("添加hashmap已报运动员失败！");
			    		log.error(e.getMessage());   
			        }
			        return group2item;
				}
				
}

