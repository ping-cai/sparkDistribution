package cn.edu.sicau.pfdistribution.service.kspcalculation;

import cn.edu.sicau.pfdistribution.service.kspcalculation.ksp.Yen;
import cn.edu.sicau.pfdistribution.service.kspcalculation.util.Path;

import java.util.List;

public class KSPUtil {
    transient
    private Graph graph;//路网图
    transient
    private List<Edge> edges;//构建路网图的边
    private List<Edge> abadonEdges;

    /**
     * @param source 进站，源地址
     * @param target 出站，目的地址
     * @param k      K短路参数
     * @return K短路List<Path> 集合
     */
    public List<Path> computeODPath(String source, String target, int k) {
        Yen yenAlgorithm = new Yen();
        List<Path> ksp = yenAlgorithm.ksp(graph, source, target, k);
        return ksp;
    }


    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

}
