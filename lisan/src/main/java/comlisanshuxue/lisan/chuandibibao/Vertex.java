package comlisanshuxue.lisan.chuandibibao;

/**
	 * 顶点
	 * 
	 * @author admin
	 *
	 */
	class Vertex {
		private String label;
		private boolean isVisited;
 
		public Vertex(String label) {
			this.label = label;
			this.isVisited = false;
		}
 
		public String getLabel() {
			return label;
		}
 
		public void setLabel(String label) {
			this.label = label;
		}
 
		public boolean isVisited() {
			return isVisited;
		}
 
		public void setVisited(boolean isVisited) {
			this.isVisited = isVisited;
		}
 
	}
