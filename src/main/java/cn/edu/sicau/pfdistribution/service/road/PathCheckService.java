package cn.edu.sicau.pfdistribution.service.road;

import cn.edu.sicau.pfdistribution.entity.DirectedEdge;
import cn.edu.sicau.pfdistribution.entity.DirectedPath;

public interface PathCheckService {
    boolean checkPath(DirectedPath path);
    boolean checkSection(DirectedEdge section);
}
