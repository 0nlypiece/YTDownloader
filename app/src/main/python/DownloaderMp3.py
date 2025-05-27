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

def download_audio_rename_mp3(url, download_path, ffmpeg_path=None):
    if os.path.exists(progress_file_path):
        os.remove(progress_file_path)

    ydl_opts = {
        'format': 'bestaudio/best',
        'outtmpl': f'{download_path}/%(title)s.%(ext)s',
        'quiet': True,
        'no_warnings': True,
        'progress_hooks': [progress_hook],
    }

    if ffmpeg_path:
        ydl_opts.update({
            'ffmpeg_location': ffmpeg_path,
            'prefer_ffmpeg': True,
            'postprocessor_args': ['-nostats', '-loglevel', 'quiet'],
            'postprocessors': [{
                'key': 'FFmpegExtractAudio',
                'preferredcodec': 'mp3',
                'preferredquality': '192',
            }],
        })

    try:
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            info_dict = ydl.extract_info(url, download=False)
            title = info_dict.get('title', 'audio')
            ydl.download([url])

        # FFmpeg yoksa yeniden adlandırma yapma
        if ffmpeg_path:
            files = os.listdir(download_path)
            for file in files:
                if file.startswith(title) and not file.endswith(".mp3"):
                    old_path = os.path.join(download_path, file)
                    new_name = os.path.splitext(file)[0] + ".mp3"
                    new_path = os.path.join(download_path, new_name)
                    os.rename(old_path, new_path)

        return "İndirme başarılı"
    except Exception as e:
        return f"Hata: {str(e)}"
