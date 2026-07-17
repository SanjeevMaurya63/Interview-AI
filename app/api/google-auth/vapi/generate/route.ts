const BACKEND_URL = "http://localhost:8080";

export async function GET() {
    return Response.json({ success: true, data: 'THANK YOU!'}, {status:200});
}

export async function POST(request: Request){
    try {
        const body = await request.json();
        
        const res = await fetch(`${BACKEND_URL}/api/interviews/generate`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });

        if (!res.ok) {
            const errData = await res.json().catch(() => ({}));
            return Response.json({ success: false, message: errData.message || "Failed to generate interview." }, { status: res.status });
        }

        const data = await res.json();
        return Response.json(data, { status: 200 });

    } catch (error) {
        console.error("Vapi generate error:", error);
        return Response.json({ success: false, message: "Internal server error" }, { status: 500 });
    }
}