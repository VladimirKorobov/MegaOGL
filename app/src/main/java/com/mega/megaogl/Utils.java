package com.mega.megaogl;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Utils {
    public enum Face {
        front,
        right,
        back,
        left,
        top,
        bottom
    }
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

    public static void AddToArrayList(List<Float> vertices, double x, double y, double z) {
        vertices.add((float)x);
        vertices.add((float)y);
        vertices.add((float)z);
    }

    private static void CalcNormals(float[] vertices, short[] indices, int offset, int stride) {
        for(int i = 0; i < vertices.length; i += stride) {
            vertices[i + offset] = 0;
            vertices[i + offset + 1] = 0;
            vertices[i + offset + 2] = 0;
        }

        final vect3 A = new vect3();
        final vect3 B = new vect3();
        final vect3 C = new vect3();

        for(int i = 0; i < indices.length; i += 3) {
            int vertOffset0 = indices[i] * stride;
            float ax = vertices[vertOffset0];
            float ay = vertices[vertOffset0 + 1];
            float az = vertices[vertOffset0 + 2];
            int vertOffset1 = indices[i + 1] * stride;
            float bx = vertices[vertOffset1];
            float by = vertices[vertOffset1 + 1];
            float bz = vertices[vertOffset1 + 2];
            int vertOffset2 = indices[i + 2] * stride;
            float cx = vertices[vertOffset2];
            float cy = vertices[vertOffset2 + 1];
            float cz = vertices[vertOffset2 + 2];

            A.init(ax, ay, az);
            B.init(bx, by, bz);
            C.init(cx, cy, cz);

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
            vertices[vertOffset0 + offset] += n.x;
            vertices[vertOffset0 + offset + 1] += n.y;
            vertices[vertOffset0 + offset + 2] += n.z;
            vertices[vertOffset1 + offset] += n.x;
            vertices[vertOffset1 + offset + 1] += n.y;
            vertices[vertOffset1 + offset + 2] += n.z;
            vertices[vertOffset2 + offset] += n.x;
            vertices[vertOffset2 + offset + 1] += n.y;
            vertices[vertOffset2 + offset + 2] += n.z;
        }
        // Normalize normals
        for( int i = 0; i < vertices.length; i += stride) {
            float length = vect3.length(new vect3(
                    vertices[i + offset],
                    vertices[i + offset + 1],
                    vertices[i + offset + 2]));
            if(length > 0) {
                vertices[i + offset] /= length;
                vertices[i + offset + 1] /= length;
                vertices[i + offset + 2] /= length;
            }
        }
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

    static class myHashTable {
        public HashMap<Long, List<Short>> map = new HashMap<>();
        public static long getKey(vect3 v) {
            return ((long)(v.x * 0x100000) << 42) + ((long)(v.y * 0x100000) << 21) +
                    ((int)(v.z * 0x100000));
        }
        public void put(long key, short value) {
            List<Short> list;
            if(map.containsKey(key)) {
                list = map.get(key);
            }
            else {
                list = new ArrayList<>();
                map.put(key, list);
            }
            list.add(value);
        }
    }

    public static void CalcSphereForCube(float[][] vertices, short[][] indices, int level) {
        level++;
        int stride = 3 + 3 + 2;
        int vertexPerFace = (level + 1) * (level + 1);
        int indexPerFace = level * level * 3 * 2;
        float[] vert = new float[vertexPerFace  * stride * 6];
        short[] ind = new short[indexPerFace * 6];
        short[] equalInd2;
        short[] equalInd3;
        final vect3 vect = new vect3();
        final float[] mat4 = new float[16];
        final float[] mat3 = new float[9];
        myHashTable equalVert = new myHashTable();

        // cycle indices
        int ix = 0;
        int iy = 0;
        int k = 0;
        int indIndex = 0;
        int vertIndex = 0;
        int equalVert2 = 0;
        int equalVert3 = 0;

        // cycle float incremets
        float dx = 2.0f / level;
        float dy = -2.0f / level;
        float dt = 1.0f / level;

        // Texture coordinate scaling
        float ktx = 0.25f;
        float kty = 1.0f/3;

        int offset;

        for (k = 0; k < 6; k++) {
            // init face parameters
            Matrix.setIdentityM(mat4, 0);
            offset = k * vertexPerFace * stride;
            float txOffset = 0;
            float tyOffset = 0;
            switch (k) {
                case 0: // front
                    txOffset = 0.25f; tyOffset = 1.0f/3;
                    break;
                case 1: // right
                    txOffset = 0.5f; tyOffset = 1.0f/3;
                    Matrix.rotateM(mat4, 0, 90, 0, 1, 0);
                    break;
                case 2: // back
                    txOffset = 0.75f; tyOffset = 1.0f/3;
                    Matrix.rotateM(mat4, 0, 180, 0, 1, 0);
                    break;
                case 3: // left
                    txOffset = 0.0f; tyOffset = 1.0f/3;
                    Matrix.rotateM(mat4, 0, 270, 0, 1, 0);
                    break;
                case 4: // top
                    txOffset = 0.25f; tyOffset = 0.0f/3;
                    Matrix.rotateM(mat4, 0, 270, 1, 0, 0);
                    break;
                case 5: // top
                    txOffset = 0.25f; tyOffset = 2.0f/3;
                    Matrix.rotateM(mat4, 0, 90, 1, 0, 0);
                    break;
            }
            Utils.mat3(mat4, mat3);
            float ty = 0;
            float y = 1;
            for (iy = 0; iy <= level; iy++, ty += dt, y += dy) {
                float tx = 0;
                float x = -1;

                for (ix = 0; ix <= level; ix++, tx += dt, x += dx, offset += stride) {
                    vect.init(x, y, 1).normalize();
                    vect.matrix3Mult(mat3);
                    equalVert.put(myHashTable.getKey(vect),(short)vertIndex);
                    vert[offset] = vert[offset + 3] = vect.x;
                    vert[offset + 1] = vert[offset + 4] = vect.y;
                    vert[offset + 2] = vert[offset + 5] = vect.z;

                    vert[offset + 6] = tx * ktx + txOffset;
                    vert[offset + 7] = ty * kty + tyOffset;

                    if(ix > 0 && iy > 0) {
                        ind[indIndex ++] = (short)((vertIndex - 1) - (level + 1));
                        ind[indIndex ++] = (short)(vertIndex -1);
                        ind[indIndex ++] = (short)(vertIndex);
                        ind[indIndex ++] = (short)(vertIndex);
                        ind[indIndex ++] = (short)(vertIndex - (level + 1));
                        ind[indIndex ++] = (short)((vertIndex - 1) - (level + 1));
                    }
                    vertIndex ++;
                }
            }
        }
        List<vect3> distortVectors = new ArrayList<>();
        List<Float> distortValues = new ArrayList<>();

        distortVectors.add(new vect3(0, 0, 1));
        distortValues.add(0.5f);
        distortVectors.add(new vect3(0, 0, -1));
        distortValues.add(1.1f);
        distortVectors.add(new vect3(1, 0, 0));
        distortValues.add(1.1f);
        distortVectors.add(new vect3(-1, 0, 0));
        distortValues.add(1.1f);
        distortVectors.add(new vect3(0, 1, 0));
        distortValues.add(1.1f);
        distortVectors.add(new vect3(0, -1, 0));
        distortValues.add(1.1f);



        distortVectors.add(new vect3(1, 1, 1).normalize());
        distortValues.add(0.9f);
        distortVectors.add(new vect3(1, -1, 1).normalize());
        distortValues.add(0.9f);

        distortVectors.add(new vect3(1, 1, -1).normalize());
        distortValues.add(0.9f);
        distortVectors.add(new vect3(1, -1, -1).normalize());

        distortValues.add(0.9f);
        distortVectors.add(new vect3(-1, 1, 1).normalize());
        distortValues.add(0.5f);

        //Distort(vert, stride, distortVectors, distortValues, 0);

        CalcNormals(vert, ind, 3, stride);
        vertices[0] = vert;
        indices[0] = ind;
        // correct normals
        Collection<List<Short>> values = equalVert.map.values();
        final vect3 n = new vect3();
        for (List<Short> v : values) {
            if(v.size() == 2 || v.size() == 3) {
                n.init(0, 0, 0);
                for (Short index : v) {
                    offset = index * stride + 3;
                    vect.init(vert[offset], vert[offset + 1], vert[offset + 2]);
                    n.add(vect);
                }
                n.normalize();
                for (Short index : v) {
                    offset = index * stride + 3;
                    vert[offset] = n.x;
                    vert[offset + 1] = n.y;
                    vert[offset + 2] = n.z;
                }
            }
            else if(v.size() == 1) {
                // Skip
            }
            else {
                // error
                return;
            }
        }
    }

    public static void Distort(float[] vert, int stride, List<vect3> extrems, List<Float> values, int rand) {
        final vect3 vect = new vect3();
        final vect3 vect1 = new vect3();
        Random random = new Random();

        for(int i = 0; i < vert.length; i += stride) {
            vect.init(vert[i], vert[i + 1], vert[i + 2]);
            float factor = 1;
            int index = 0;
            int maxIndex = 0;
            float max = 1;
            for(vect3 v : extrems) {
                float f = vect3.dot(v, vect);

                if(f > 0) {
                    factor += Math.pow(f, 40) * (values.get(index) - 1);

                }
                index ++;
            }
            //max = (float)Math.pow(max, 2);
            //factor = (1 + max) /  (extrems.size() + 1);

            vert[i] *= factor; vert[i + 1] *= factor; vert[i + 2] *= factor;
        }
    }
}
