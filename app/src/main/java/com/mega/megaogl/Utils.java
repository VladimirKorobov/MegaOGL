package com.mega.megaogl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    static class vect2 {
        public float x, y;
        public vect2() {}
        public vect2(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public static float dot(float x0, float y0, float x1, float y1)
        {
            return x1 * x1 + y1 * y1;
        }
    }
    static class vect3 {
        public float x, y, z;
        public vect3() {}
        public vect3(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public vect3(double x, double y, double z) {
            this.x = (float)x;
            this.y = (float)y;
            this.z = (float)z;
        }
        public static vect3 cross(vect3 u, vect3 v) {
            return new vect3(u.y*v.z - u.z*v.y, u.z*v.x - u.x*v.z, u.x*v.y - u.y*v.x);
        }

        public static float dot(vect3 u, vect3 v) { return (u.x*v.x + u.y*v.y + u.z*v.z); }
        public static vect3 mult(vect3 u,float v) { return new vect3(u.x*v,u.y*v,u.z*v); }
        public vect3 mult(float v) {
            this.x*=v;this.y*=v;this.z*=v;
            return this;
        }
        public static vect3 subt(vect3 u, vect3 v) { return new vect3(u.x - v.x, u.y - v.y, u.z - v.z); }
        public static vect3 sum(vect3 u, vect3 v) { return new vect3(u.x + v.x, u.y + v.y, u.z + v.z); }
        public vect3 add(vect3 v) {
            this.x += v.x; this.y += v.y; this.z += v.z;
            return this;
        }
        public static float length(vect3 u) { return (float)Math.sqrt(u.x*u.x + u.y*u.y + u.z*u.z); }
        public float length() { return (float)Math.sqrt(x*x + y*y + z*z); }
        public static vect3 normalize(vect3 u) { return vect3.mult(u, 1.0f  / length(u)); }
        public vect3 normalize() {
            vect3 u = vect3.mult(this, 1.0f  / vect3.length(this));
            this.x = u.x;
            this.y = u.y;
            this.z = u.z;
            return this;
        }
        public vect3 init(float x, float y, float z) {this.x = x; this.y = y; this.z = z; return this; }

        public static vect3 matrixMult(float[] mat, vect3 u) {
            vect3 v = new vect3();
            v.x = mat[0] * u.x + mat[4] * u.y + mat[8] * u.z + mat[12];
            v.y = mat[1] * u.x + mat[5] * u.y + mat[9] * u.z + mat[13];
            v.z = mat[2] * u.x + mat[6] * u.y + mat[10] * u.z + mat[14];
            return v;
        }
        public vect3 matrixMult(float[] mat) {
            float x = mat[0] * this.x + mat[4] * this.y + mat[8] * this.z + mat[12];
            float y = mat[1] * this.x + mat[5] * this.y + mat[9] * this.z + mat[13];
            float z = mat[2] * this.x + mat[6] * this.y + mat[10] * this.z + mat[14];
            this.x = x; this.y = y; this.z = z;
            return this;
        }
        public vect3 matrix3Mult(float[] mat) {
            float x = mat[0] * this.x + mat[3] * this.y + mat[6] * this.z;
            float y = mat[1] * this.x + mat[4] * this.y + mat[7] * this.z;
            float z = mat[2] * this.x + mat[5] * this.y + mat[8] * this.z;
            this.x = x; this.y = y; this.z = z;
            return this;
        }
    }

    public static void mat3(float[] mat4, float[] mat3) {
        mat3[0] = mat4[0]; mat3[3] = mat4[4]; mat3[6] = mat4[8];
        mat3[1] = mat4[1]; mat3[4] = mat4[5]; mat3[7] = mat4[9];
        mat3[2] = mat4[2]; mat3[5] = mat4[6]; mat3[8] = mat4[10];
    }
    public static float determinant(float []m){
        return
                + m[0] * (m[4] * m[8] - m[7] * m[5])
                - m[3] * (m[1] * m[8] - m[7] * m[2])
                + m[6] * (m[1] * m[5] - m[4] * m[2]);
    }
    public static void inverse(float[] m, float[] inverse) {
        float Determinant = 1.0f/determinant(m);
        inverse[0] = + (m[4] * m[8] - m[7] * m[5]) * Determinant;
        inverse[3] = - (m[3] * m[8] - m[6] * m[5]) * Determinant;
        inverse[6] = + (m[3] * m[7] - m[6] * m[4]) * Determinant;
        inverse[1] = - (m[1] * m[8] - m[7] * m[2]) * Determinant;
        inverse[4] = + (m[0] * m[8] - m[6] * m[2]) * Determinant;
        inverse[7] = - (m[0] * m[7] - m[6] * m[1]) * Determinant;
        inverse[2] = + (m[1] * m[5] - m[4] * m[2]) * Determinant;
        inverse[5] = - (m[0] * m[5] - m[3] * m[2]) * Determinant;
        inverse[8] = + (m[0] * m[4] - m[3] * m[1]) * Determinant;
    }
    public static void transpose(float []m, float[]result){
        result[0] = m[0];
        result[1] = m[3];
        result[2] = m[6];

        result[3] = m[1];
        result[4] = m[4];
        result[5] = m[7];

        result[6] = m[2];
        result[7] = m[5];
        result[8] = m[8];
    }
    public static void normalize(float[] mat3) {
        float len0 = (float)Math.sqrt(mat3[0]*mat3[0] + mat3[1]*mat3[1] + mat3[2]*mat3[2]);
        float len1 = (float)Math.sqrt(mat3[3]*mat3[3] + mat3[4]*mat3[4] + mat3[5]*mat3[5]);
        float len2 = (float)Math.sqrt(mat3[6]*mat3[6] + mat3[7]*mat3[7] + mat3[8]*mat3[8]);

        mat3[0] /= len0; mat3[3] /= len1; mat3[6] /= len2;
        mat3[1] /= len0; mat3[4] /= len1; mat3[7] /= len2;
        mat3[2] /= len0; mat3[5] /= len1; mat3[8] /= len2;
    }

    public static class edge {
        public short indexStart;
        public short indexEnd;
        public short indexMiddle;
        public edge childEdge0;
        public edge childEdge1;

        public edge( short indexStart, short indexEnd) {
            this.indexStart = indexStart;
            this.indexEnd = indexEnd;
            indexMiddle = -1;
        }
        public void createChilds(short index) {
            childEdge0 = new edge(indexStart, index);
            childEdge1 = new edge(index, indexEnd);
            indexMiddle = index;
        }
        public boolean hasChild() {
            return childEdge0 != null && childEdge1 != null;
        }
    }

    public static class face4 {
        edge edge0;
        edge edge1;
        edge edge2;
        edge edge3;
        short index0;
        short index1;
        short index2;
        short index3;
        vect2 tex = new vect2();
        List<Float> vertices;
        List<Short> indices;
        List<Float> texCoord;
        public face4(edge edge0, edge edge1, edge edge2, edge edge3,
                     short index0, short index1, short index2, short index3,
                     List<Float> vertices, List<Short> indices, List<Float> texCoord,
                     int level) {
            this.edge0 = edge0;
            this.edge1 = edge1;
            this.edge2 = edge2;
            this.edge3 = edge3;
            this.index0 = index0;
            this.index1 = index1;
            this.index2 = index2;
            this.index3 = index3;
            this.vertices = vertices;
            this.indices = indices;
            this.texCoord = texCoord;
        }

        private short addVertexToCenter(int level) {
            // assuming, it's level == 1
            vect3 newVert = new vect3(vertices.get(index0 * 3), vertices.get(index0 * 3 + 1), vertices.get(index0 * 3 + 2));
            vect3 v1 = new vect3(vertices.get(index1 * 3), vertices.get(index1 * 3 + 1), vertices.get(index1 * 3 + 2));
            vect3 v2 = new vect3(vertices.get(index2 * 3), vertices.get(index2 * 3 + 1), vertices.get(index2 * 3 + 2));
            vect3 v3 = new vect3(vertices.get(index3 * 3), vertices.get(index3 * 3 + 1), vertices.get(index3 * 3 + 2));

            newVert.add(v1).add(v2).add(v3).normalize();
            short lastIndex = (short)(vertices.size() / 3);
            // add vertex
            vertices.add(newVert.x); vertices.add(newVert.y); vertices.add(newVert.z);
            Texture.getCoord3D(newVert, tex);
            texCoord.add(tex.x); texCoord.add(tex.y);

            if(level == 1) {
                // add four triangles
                indices.add(index0);
                indices.add(index1);
                indices.add(lastIndex);
                indices.add(index1);
                indices.add(index2);
                indices.add(lastIndex);
                indices.add(index2);
                indices.add(index3);
                indices.add(lastIndex);
                indices.add(index3);
                indices.add(index0);
                indices.add(lastIndex);
            }
            return lastIndex;
        }

        private boolean edgesHaveChilds() {
            return edge0.hasChild() && edge1.hasChild() && edge2.hasChild() && edge3.hasChild();
        }

        private void createMiddleEdgeVertex(edge _edge) {
            if(_edge.indexMiddle == -1) {
                vect3 v0 = new vect3(
                        vertices.get(_edge.indexStart * 3),
                        vertices.get(_edge.indexStart * 3 + 1),
                        vertices.get(_edge.indexStart * 3 + 2));
                vect3 v1 = new vect3(
                        vertices.get(_edge.indexEnd * 3),
                        vertices.get(_edge.indexEnd * 3 + 1),
                        vertices.get(_edge.indexEnd * 3 + 2));

                vect3 v = vect3.sum(v0, v1);
                v.normalize();
                _edge.createChilds((short)(vertices.size()/3));
                vertices.add(v.x);
                vertices.add(v.y);
                vertices.add(v.z);
                Texture.getCoord3D(v, tex);
                texCoord.add(tex.x); texCoord.add(tex.y);
            }
        }

        public void createChilds(int level) {
            short newIndex = addVertexToCenter(level);
            if(level > 1) {
                // Assuming child for edges has alreadybeen created,
                createMiddleEdgeVertex(edge0);
                createMiddleEdgeVertex(edge1);
                createMiddleEdgeVertex(edge2);
                createMiddleEdgeVertex(edge3);

                edge newEdge0 = new edge(edge0.indexMiddle, newIndex);
                edge newEdge1 = new edge(edge1.indexMiddle, newIndex);
                edge newEdge2 = new edge(edge2.indexMiddle, newIndex);
                edge newEdge3 = new edge(edge3.indexMiddle, newIndex);

                // Create four new faces
                edge tempEdge0 = edge0.indexStart == index0 ? edge0.childEdge0 : edge0.childEdge1;
                edge tempEdge1 = edge3.indexStart == index0 ? edge3.childEdge0 : edge3.childEdge1;
                face4 f0 = new face4(tempEdge0, newEdge0, newEdge3, tempEdge1,
                        index0, edge0.indexMiddle, newIndex, edge3.indexMiddle,
                        vertices, indices, texCoord, level);
                tempEdge0 = edge0.indexStart == index1 ? edge0.childEdge0 : edge0.childEdge1;
                tempEdge1 = edge1.indexStart == index1 ? edge1.childEdge0 : edge1.childEdge1;
                face4 f1 = new face4(tempEdge0, tempEdge1, newEdge1, newEdge0,
                        edge0.indexMiddle, index1, edge1.indexMiddle, newIndex,
                        vertices, indices, texCoord, level);
                tempEdge0 = edge1.indexStart == index2 ? edge1.childEdge0 : edge1.childEdge1;
                tempEdge1 = edge2.indexStart == index2 ? edge2.childEdge0 : edge2.childEdge1;
                face4 f2 = new face4(tempEdge0, tempEdge1, newEdge2, newEdge1,
                        edge1.indexMiddle, index2, edge2.indexMiddle, newIndex,
                        vertices, indices, texCoord, level);
                tempEdge0 = edge2.indexStart == index3 ? edge2.childEdge0 : edge2.childEdge1;
                tempEdge1 = edge3.indexStart == index3 ? edge3.childEdge0 : edge3.childEdge1;
                face4 f3 = new face4(tempEdge0, tempEdge1, newEdge3, newEdge2,
                        edge2.indexMiddle, index3, edge3.indexMiddle, newIndex,
                        vertices, indices, texCoord, level);

                if (--level > 0) {
                    f0.createChilds(level);
                    f1.createChilds(level);
                    f2.createChilds(level);
                    f3.createChilds(level);
                }
            }
        }
    }

    public static void AddToArrayList(List<Float> vertices, double x, double y, double z) {
        vertices.add((float)x);
        vertices.add((float)y);
        vertices.add((float)z);
    }

    private static void CalcNormals(List<Float> vertices, List<Short> indices, float[] normals) {

        for(int i = 0; i < normals.length; i ++) {
            normals[i] = 0;
        }

        for(int i = 0; i < indices.size(); i += 3) {
            int vertindex0 = indices.get(i);
            int vertindex1 = indices.get(i + 1);
            int vertindex2 = indices.get(i + 2);

            float a0 = vertices.get(vertindex0 * 3);
            float b0 = vertices.get(vertindex1 * 3);
            float c0 = vertices.get(vertindex2 * 3);

            float a1 = vertices.get(vertindex0 * 3 + 1);
            float b1 = vertices.get(vertindex1 * 3 + 1);
            float c1 = vertices.get(vertindex2 * 3 + 1);

            float a2 = vertices.get(vertindex0 * 3 + 2);
            float b2 = vertices.get(vertindex1 * 3 + 2);
            float c2 = vertices.get(vertindex2 * 3 + 2);

            vect3 A = new vect3(a0, a1, a2);
            vect3 B = new vect3(b0, b1, b2);
            vect3 C = new vect3(c0, c1, c2);

            vect3 edgeab = vect3.subt(B,A);
            vect3 edgeac = vect3.subt(C,A);
            vect3 edgebc = vect3.subt(C,B);

            vect3 n = new vect3(0, 0, 1);

            if (vect3.length(edgeab) > 1.e-6)
            {
                if (vect3.length(edgeac) > 1.e-6)
                {
                    vect3 v1 = vect3.normalize(edgeab);
                    vect3 v2 = vect3.normalize(edgeac);

                    //n = vect3.cross(vect3.normalize(edgeab), vect3.normalize(edgeac));
                    n = vect3.cross(v1, v2);
                }
                else if (vect3.length(edgebc) > 1.e-6)
                {
                    n = vect3.cross(vect3.normalize(edgebc), vect3.normalize(edgeab));
                }
            }
            else {
                if (vect3.length(edgeac) > 1.e-6 && vect3.length(edgebc) > 1.e-6) {
                    n = vect3.cross(vect3.normalize(edgebc), vect3.normalize(edgeac));
                }
            }
            n = vect3.normalize(n);
            normals[vertindex0 * 3] += n.x;
            normals[vertindex0 * 3 + 1] += n.y;
            normals[vertindex0 * 3 + 2] += n.z;
            normals[vertindex1 * 3] += n.x;
            normals[vertindex1 * 3 + 1] += n.y;
            normals[vertindex1 * 3 + 2] += n.z;
            normals[vertindex2 * 3] += n.x;
            normals[vertindex2 * 3 + 1] += n.y;
            normals[vertindex2 * 3 + 2] += n.z;
        }
        // Normalize normals
        for( int i = 0; i < normals.length; i += 3) {
            float length = vect3.length(new vect3(normals[i], normals[i + 1], normals[i + 2]));
            if(length > 0) {
                normals[i] /= length;
                normals[i + 1] /= length;
                normals[i + 2] /= length;
            }
        }
    }

/*
    private static void CalcNormals(float[] vertices, short[] indices, float[] normals) {

        for(int i = 0; i < normals.length; i ++) {
            normals[i] = 0;
        }

        for(int i = 0; i < indices.length; i += 3) {
            int vertindex0 = indices[i];
            int vertindex1 = indices[i + 1];
            int vertindex2 = indices[i + 2];

            float a0 = vertices[vertindex0 * 3];
            float b0 = vertices[vertindex1 * 3];
            float c0 = vertices[vertindex2 * 3];

            float a1 = vertices[vertindex0 * 3 + 1];
            float b1 = vertices[vertindex1 * 3 + 1];
            float c1 = vertices[vertindex2 * 3 + 1];

            float a2 = vertices[vertindex0 * 3 + 2];
            float b2 = vertices[vertindex1 * 3 + 2];
            float c2 = vertices[vertindex2 * 3 + 2];

            vect3 A = new vect3(a0, a1, a2);
            vect3 B = new vect3(b0, b1, b2);
            vect3 C = new vect3(c0, c1, c2);

            vect3 edgeab = vect3.subt(B,A);
            vect3 edgeac = vect3.subt(C,A);
            vect3 edgebc = vect3.subt(C,B);

            vect3 n = new vect3(0, 0, 1);

            if (vect3.length(edgeab) > 1.e-6)
            {
                if (vect3.length(edgeac) > 1.e-6)
                {
                    vect3 v1 = vect3.normalize(edgeab);
                    vect3 v2 = vect3.normalize(edgeac);

                    //n = vect3.cross(vect3.normalize(edgeab), vect3.normalize(edgeac));
                    n = vect3.cross(v1, v2);
                }
                else if (vect3.length(edgebc) > 1.e-6)
                {
                    n = vect3.cross(vect3.normalize(edgebc), vect3.normalize(edgeab));
                }
            }
            else {
                if (vect3.length(edgeac) > 1.e-6 && vect3.length(edgebc) > 1.e-6) {
                    n = vect3.cross(vect3.normalize(edgebc), vect3.normalize(edgeac));
                }
            }
            n = vect3.normalize(n);
            normals[vertindex0 * 3] += n.x;
            normals[vertindex0 * 3 + 1] += n.y;
            normals[vertindex0 * 3 + 2] += n.z;
            normals[vertindex1 * 3] += n.x;
            normals[vertindex1 * 3 + 1] += n.y;
            normals[vertindex1 * 3 + 2] += n.z;
            normals[vertindex2 * 3] += n.x;
            normals[vertindex2 * 3 + 1] += n.y;
            normals[vertindex2 * 3 + 2] += n.z;
        }
        // Normalize normals
        for( int i = 0; i < normals.length; i += 3) {
            float length = vect3.length(new vect3(normals[i], normals[i + 1], normals[i + 2]));
            if(length > 0) {
                normals[i] /= length;
                normals[i + 1] /= length;
                normals[i + 2] /= length;
            }
        }
    }
*/
    public static void CalcRect1(float[][] vertices, short[][] indices)
    {
        vertices[0] = new float[12 * 2];
        indices[0] = new short[6];

        vertices[0][0] = -0.5f;
        vertices[0][1] = -0.5f;
        vertices[0][2] = 0;

        vertices[0][4] = -0.5f;
        vertices[0][5] = -0.5f;
        vertices[0][6] = 0;

        float len = (float)Math.sqrt(vertices[0][4] * vertices[0][4] +
                vertices[0][5] * vertices[0][5] + vertices[0][6] * vertices[0][6]);

        vertices[0][4] /= len;
        vertices[0][5] /= len;
        vertices[0][6] /= len;


        vertices[0][6] = -0.5f;
        vertices[0][7] = 0.5f;
        vertices[0][8] = 0;

        vertices[0][9] = -0.5f;
        vertices[0][10] = 0.5f;
        vertices[0][11] = 0;

        len = (float)Math.sqrt(vertices[0][9] * vertices[0][9] +
                vertices[0][10] * vertices[0][10] + vertices[0][11] * vertices[0][11]);

        vertices[0][9] /= len;
        vertices[0][10] /= len;
        vertices[0][11] /= len;


        vertices[0][12] = 0.5f;
        vertices[0][13] = 0.5f;
        vertices[0][14] = 0;

        vertices[0][15] = 0.5f;
        vertices[0][16] = 0.5f;
        vertices[0][17] = 0;

        len = (float)Math.sqrt(vertices[0][15] * vertices[0][15] +
                vertices[0][16] * vertices[0][16] + vertices[0][17] * vertices[0][17]);

        vertices[0][15] /= len;
        vertices[0][16] /= len;
        vertices[0][17] /= len;

        vertices[0][18] = 0.5f;
        vertices[0][19] = -0.5f;
        vertices[0][20] = 0;

        vertices[0][21] = 0.5f;
        vertices[0][22] = -0.5f;
        vertices[0][23] = 0;

        len = (float)Math.sqrt(vertices[0][21] * vertices[0][21] +
                vertices[0][22] * vertices[0][22] + vertices[0][23] * vertices[0][23]);

        vertices[0][21] /= len;
        vertices[0][22] /= len;
        vertices[0][23] /= len;

        indices[0][0] = 0;
        indices[0][1] = 3;
        indices[0][2] = 1;
        indices[0][3] = 3;
        indices[0][4] = 2;
        indices[0][5] = 1;
    }


    public static void CalcRect(float[][] vertices, short[][] indices)
    {
        vertices[0] = new float[12 * 2];
        indices[0] = new short[6];

        vertices[0][0] = -0.5f;
        vertices[0][1] = -0.5f;
        vertices[0][2] = 0;

        vertices[0][6] = -0.5f;
        vertices[0][7] = 0.5f;
        vertices[0][8] = 0;

        vertices[0][12] = 0.5f;
        vertices[0][13] = 0.5f;
        vertices[0][14] = 0;

        vertices[0][18] = 0.5f;
        vertices[0][19] = -0.5f;
        vertices[0][20] = 0;

        vertices[0][5] = vertices[0][11] = vertices[0][17] = vertices[0][23] = 1.0f;
        indices[0][0] = 0;
        indices[0][1] = 3;
        indices[0][2] = 1;
        indices[0][3] = 3;
        indices[0][4] = 2;
        indices[0][5] = 1;
    }

    public static void CalcCube(float[][] vertices, short[][] indices) {
        vertices[0] = new float[8 * 3 * 2];
        indices[0] = new short[6 * 6];

        int i = 0;

        vertices[0][i++] = -1.0f; vertices[0][i++] = -1.0f; vertices[0][i++] = 1.0f;
        vertices[0][i++] = -1.0f; vertices[0][i++] = -1.0f; vertices[0][i++] = 1.0f;

        vertices[0][i++] = -1.0f; vertices[0][i++] = 1.0f; vertices[0][i++] = 1.0f;
        vertices[0][i++] = -1.0f; vertices[0][i++] = 1.0f; vertices[0][i++] = 1.0f;

        vertices[0][i++] = 1.0f; vertices[0][i++] = 1.0f; vertices[0][i++] = 1.0f;
        vertices[0][i++] = 1.0f; vertices[0][i++] = 1.0f; vertices[0][i++] = 1.0f;

        vertices[0][i++] = 1.0f; vertices[0][i++] = -1.0f; vertices[0][i++] = 1.0f;
        vertices[0][i++] = 1.0f; vertices[0][i++] = -1.0f; vertices[0][i++] = 1.0f;

        vertices[0][i++] = -1.0f; vertices[0][i++] = -1.0f; vertices[0][i++] = -1.0f;
        vertices[0][i++] = -1.0f; vertices[0][i++] = -1.0f; vertices[0][i++] = -1.0f;

        vertices[0][i++] = -1.0f; vertices[0][i++] = 1.0f; vertices[0][i++] = -1.0f;
        vertices[0][i++] = -1.0f; vertices[0][i++] = 1.0f; vertices[0][i++] = -1.0f;

        vertices[0][i++] = 1.0f; vertices[0][i++] = 1.0f; vertices[0][i++] = -1.0f;
        vertices[0][i++] = 1.0f; vertices[0][i++] = 1.0f; vertices[0][i++] = -1.0f;

        vertices[0][i++] = 1.0f; vertices[0][i++] = -1.0f; vertices[0][i++] = -1.0f;
        vertices[0][i++] = 1.0f; vertices[0][i++] = -1.0f; vertices[0][i++] = -1.0f;

        // Normalize
        float[] vert = vertices[0];
        for(i = 0; i < 8; i ++)
        {
            float len = (float)Math.sqrt(vert[i * 6 + 3] * vert[i * 6 + 3] +
                    vert[i * 6 + 4] * vert[i * 6 + 4] +
                    vert[i * 6 + 5] * vert[i * 6 + 5]);
            if(len > 0) {
                vert[i * 6 + 3] /= len;
                vert[i * 6 + 4] /= len;
                vert[i * 6 + 5] /= len;
            }
        }

        i = 0;
        indices[0][i++] = 0; indices[0][i++] = 3; indices[0][i++] = 1;
        indices[0][i++] = 3; indices[0][i++] = 2; indices[0][i++] = 1;

        indices[0][i++] = 3; indices[0][i++] = 7; indices[0][i++] = 2;
        indices[0][i++] = 7; indices[0][i++] = 6; indices[0][i++] = 2;

        indices[0][i++] =7; indices[0][i++] = 4; indices[0][i++] = 6;
        indices[0][i++] = 4; indices[0][i++] = 5; indices[0][i++] = 6;

        indices[0][i++] =4; indices[0][i++] = 0; indices[0][i++] = 5;
        indices[0][i++] = 0; indices[0][i++] = 1; indices[0][i++] = 5;

        indices[0][i++] =1; indices[0][i++] = 2; indices[0][i++] = 5;
        indices[0][i++] = 2; indices[0][i++] = 6; indices[0][i++] = 5;

        indices[0][i++] =4; indices[0][i++] = 7; indices[0][i++] = 0;
        indices[0][i++] = 7; indices[0][i++] = 3; indices[0][i++] = 0;
    }

    public static void CalcSphere(float[][] vertices, short[][] indices, int level) {
        // Create base cube
        List<Float> vertexList =  new ArrayList<>();
        List<Float> texcoordList =  new ArrayList<>();
        List<Short> indicesList =  new ArrayList<>();
        vect2 tex = new vect2();

        vect3 u = new vect3();
        u.init(-1, 1, 1).normalize();
        vertexList.add(u.x); vertexList.add(u.y); vertexList.add(u.z);
        Texture.getCoord3D(u, tex);
        texcoordList.add(tex.x); texcoordList.add(tex.y);
        u.init(-1, -1, 1).normalize();
        vertexList.add(u.x); vertexList.add(u.y); vertexList.add(u.z);
        Texture.getCoord3D(u, tex);
        texcoordList.add(tex.x); texcoordList.add(tex.y);
        u.init(1, -1, 1).normalize();
        vertexList.add(u.x); vertexList.add(u.y); vertexList.add(u.z);
        Texture.getCoord3D(u, tex);
        texcoordList.add(tex.x); texcoordList.add(tex.y);
        u.init(1, 1, 1).normalize();
        vertexList.add(u.x); vertexList.add(u.y); vertexList.add(u.z);
        Texture.getCoord3D(u, tex);
        texcoordList.add(tex.x); texcoordList.add(tex.y);

        u.init(-1, 1, -1).normalize();
        vertexList.add(u.x); vertexList.add(u.y); vertexList.add(u.z);
        Texture.getCoord3D(u, tex);
        texcoordList.add(tex.x); texcoordList.add(tex.y);
        u.init(-1, -1, -1).normalize();
        vertexList.add(u.x); vertexList.add(u.y); vertexList.add(u.z);
        Texture.getCoord3D(u, tex);
        texcoordList.add(tex.x); texcoordList.add(tex.y);
        u.init(1, -1, -1).normalize();
        vertexList.add(u.x); vertexList.add(u.y); vertexList.add(u.z);
        Texture.getCoord3D(u, tex);
        texcoordList.add(tex.x); texcoordList.add(tex.y);
        u.init(1, 1, -1).normalize();
        vertexList.add(u.x); vertexList.add(u.y); vertexList.add(u.z);
        Texture.getCoord3D(u, tex);
        texcoordList.add(tex.x); texcoordList.add(tex.y);

        // Create edges
        edge e0 = new edge((short)0, (short)1);
        edge e1 = new edge((short)1, (short)2);
        edge e2 = new edge((short)2, (short)3);
        edge e3 = new edge((short)3, (short)0);
        edge e4 = new edge((short)4, (short)5);
        edge e5 = new edge((short)5, (short)6);
        edge e6 = new edge((short)6, (short)7);
        edge e7 = new edge((short)7, (short)4);
        edge e8 = new edge((short)0, (short)4);
        edge e9 = new edge((short)1, (short)5);
        edge e10 = new edge((short)2, (short)6);
        edge e11 = new edge((short)3, (short)7);


        face4 f0 = new Utils.face4( // front
                e0, e1, e2, e3,
                (short)0, (short)1, (short)2, (short)3,
                vertexList, indicesList, texcoordList, level);

        face4 f1 = new Utils.face4( // right
                e2, e10, e6, e11,
                (short)3, (short)2, (short)6, (short)7,
                vertexList, indicesList, texcoordList, level);


        face4 f2 = new Utils.face4( // back
                e6, e5, e4, e7,
                (short)7, (short)6, (short)5, (short)4,
                vertexList, indicesList, texcoordList, level);

        face4 f3 = new Utils.face4( // left
                e8, e4, e9, e0,
                (short)0, (short)4, (short)5, (short)1,
                vertexList, indicesList, texcoordList, level);

        face4 f4 = new Utils.face4( //top
                e3, e11, e7, e8,
                (short)0, (short)3, (short)7, (short)4,
                vertexList, indicesList, texcoordList, level);


        face4 f5 = new Utils.face4( // bottom
                e9, e5, e10, e1,
                (short)1, (short)5, (short)6, (short)2,
                vertexList, indicesList, texcoordList, level);

        f0.createChilds(level);
        f1.createChilds(level);
        f2.createChilds(level);
        f3.createChilds(level);
        f4.createChilds(level);
        f5.createChilds(level);

        float[] normals = new float[vertexList.size()];
        Utils.CalcNormals(vertexList, indicesList, normals);

        vertices[0] = new float[vertexList.size() * 2 + texcoordList.size()];
        indices[0] = new short[indicesList.size()];

        int j = 0;
        int i = 0;
        for(; i < vertices[0].length; j ++ ) {
            vertices[0][i++] = vertexList.get(j * 3);
            vertices[0][i++] = vertexList.get(j * 3 + 1);
            vertices[0][i++] = vertexList.get(j * 3 + 2);
            vertices[0][i++] = normals[j * 3];
            vertices[0][i++] = normals[j * 3 + 1];
            vertices[0][i++] = normals[j * 3 + 2];
            vertices[0][i++] = texcoordList.get(j * 2);
            vertices[0][i++] = texcoordList.get(j * 2 + 1);
        }
        i = 0;
        for (Short f : indicesList) {
            indices[0][i++] = (f != null ? f : 0);
        }
    }
}
