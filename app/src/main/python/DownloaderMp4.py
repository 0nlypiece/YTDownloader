import yt_dlp
import os

progress_file_path = "/storage/emulated/0/Android/data/com.example.ytdownloader/files/progress.txt"

def progress_hook(d):
    try:
        if d['status'] == 'downloading':
            percent = d.get('_percent_str', '0.0%').strip().replace('%', '')
            with open(progress_file_path, 'w') as f:
                f.write(str(int(float(percent))))
                f.flush()
                os.fsync(f.fileno())
        elif d['status'] == 'finished':
            with open(progress_file_path, 'w') as f:
                f.write("100")
                f.flush()
                os.fsync(f.fileno())
    except Exception:
        pass

def download_mp4(url, download_path):
    if os.path.exists(progress_file_path):
        os.remove(progress_file_path)

    ydl_opts = {
        'format': 'best[ext=mp4][vcodec!=none][acodec!=none]',
        'outtmpl': f'{download_path}/%(title)s.%(ext)s',
        'quiet': True,
        'no_warnings': True,
        'progress_hooks': [progress_hook],
    }

    try:
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            ydl.download([url])
        return "İndirme başarılı"
    except Exception as e:
        return f"Hata: {str(e)}"
