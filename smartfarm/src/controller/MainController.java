package controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import dto.LoginUserDTO;
import dto.MemberDTO;
import dto.UserSessionDTO;
import mqtt.MqttManager;
import service.MemberService;
import service.MemberServiceImpl;
import util.ConsoleUtils;
import view.MainView;

public class MainController {
	private UserSessionDTO currentUser = null; // 현재 로그인한 사용자 정보
    private final MainView view = new MainView(); // 화면을 담당할 View 객체
    private final MemberService service = new MemberServiceImpl();
    private MqttManager mqttManager;
    public void run() {
        while (true) {
            if (currentUser == null) {
                // 로그인되지 않았을 때의 로직 처리
                handleInitialMenu();
            } else {
                // 로그인된 후의 로직 처리
                handleMainMenu();
            }
        }
    }
    private void handleInitialMenu() {
    	ConsoleUtils.clearConsole();
        String choice = view.showInitialMenu(); // View는 메뉴를 보여주고 입력만 받아서 전달
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "9":
                exitProgram();
                break;
            default:
                view.showMessage("(!) 잘못된 입력입니다.");
        }
    }

    private void login() {
    	ConsoleUtils.clearConsole();
    	
    	LoginUserDTO loginUser = view.handleLogin();
    	MemberDTO loginSuccessUser = service.login(loginUser.getUserId(),
    									loginUser.getPass());
    	//로그인 성공하면 세션에 로그인사용자정보를 담고 Mqtt Subscriber를 실행함
		if(loginSuccessUser!=null) {
			currentUser = new UserSessionDTO(loginSuccessUser);
			 System.out.println("\nMQTT 서비스에 연결을 시작합니다...");
	         mqttManager = new MqttManager(currentUser.getLoginUser().getUserId());
			handleMainMenu();
		}else {
			JOptionPane.showMessageDialog(null, "로그인실패");
			view.handleLogin();
		}
	}
	private void register() {
		ConsoleUtils.clearConsole();
        // View에 현재 사용자 이름을 넘겨주어 메뉴를 보여주게 함
        MemberDTO user = view.showRegistrationForm();
        int result = service.register(user);
        new Thread(() -> {
            if (result >= 1) {
                JOptionPane.showMessageDialog(null, "회원가입이 완료됐습니다.");
            } else {
                JOptionPane.showMessageDialog(null, "회원가입에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }).start(); // 스레드 시작!
	}
	private void handleMainMenu() {
		ConsoleUtils.clearConsole();
        // View에 현재 사용자 이름을 넘겨주어 메뉴를 보여주게 함
        String choice = view.showMainMenu(currentUser.getLoginUser().getName());
        switch (choice) {
            case "1":
                // deviceControl();
                view.showMessage("💡 장치 제어 메뉴입니다.");
                break;
            case "2":
                // analyzeSensorData();
                view.showMessage("📊 센서 데이터 분석 메뉴입니다.");
                break;
            case "3":
                // configureSettings();
                view.showMessage("⚙️ 환경 설정 메뉴입니다.");
                break;
            case "8":
                logout();
                break;
            case "9":
                exitProgram();
                break;
            default:
                view.showMessage("(!) 잘못된 입력입니다.");
        }
    }
	private void logout() {
		// TODO Auto-generated method stub
		
	}
	private void exitProgram() {
		// TODO Auto-generated method stub
		
	}
}
